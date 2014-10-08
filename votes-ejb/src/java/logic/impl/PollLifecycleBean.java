/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.impl;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import persistence.PollAccess;
import persistence.TokenAccess;
import persistence.entities.DecisionMode;
import persistence.entities.Item;
import persistence.entities.ItemOption;
import persistence.entities.Participant;
import persistence.entities.Poll;
import persistence.entities.PollState;
import persistence.entities.Token;
import util.TimerInfo;
import util.TimerInfo.TimeoutCause;

/**
 * An EJB responsible for handling various events in the lifecycle of a poll
 *
 * @author nico
 */
@Stateless
@LocalBean
public class PollLifecycleBean implements Serializable {

    @Resource
    TimerService timerService;
    @Resource(lookup = "mail/uniko-mail")
    private Session mailSession;
    @EJB
    PollAccess pollAccess;
    @EJB
    TokenAccess tokenAccess;
    @Resource(lookup = "rootURL")
    private String rootUrl;

    /**
     * if a vote is finished prematurely, extendend or cancelled the
     * corresponding timer events have to be cancelled too
     *
     * @param pollID
     * @param cause
     */
    public void cancelTimer(Long pollID, TimeoutCause cause) {
        for (Timer timer : timerService.getTimers()) {
            TimerInfo info = (TimerInfo) timer.getInfo();
            if (info.getPollID().equals(pollID) && info.getCause() == cause) {
                timer.cancel();
            }
        }
    }

    /**
     * Whenever a timeout is observed the pollID and the cause for the timeout
     * are read from the TimerInfo object of the corresponding timer Here the
     * TimeoutCause Enum is used to indicate the timeout cause : START denotes
     * that the startDate of the poll has come and the poll should be started,
     * REMIND denotes that the reminderDate of the poll has come and END denotes
     * that the endDate of the poll has come.
     *
     * @param timer
     */
    @Timeout
    public void handleTimeout(Timer timer) {
        TimerInfo tInfo = (TimerInfo) timer.getInfo();
        Long pollID = tInfo.getPollID();

        if (tInfo.getCause() == TimeoutCause.START) {
            handleVoteRunning(pollID);
        } else if (tInfo.getCause() == TimeoutCause.REMIND) {
            sendEmailReminders(pollID);
        } else if (tInfo.getCause() == TimeoutCause.END) {
            handleVoteFinished(pollID);
        }
    }

    /**
     * creates a timer which keeps track of the startDate of its corresponding
     * poll
     *
     * @param pollID
     * @param startDate
     */
    public void createVoteStartedTimeout(Long pollID, Date startDate) {
        TimerConfig tCfg = new TimerConfig();
        TimerInfo timerInfo = new TimerInfo(TimeoutCause.START, pollID);
        tCfg.setInfo(timerInfo);
        timerService.createSingleActionTimer(startDate, tCfg);
    }

    /**
     * creates a timer which keeps track of the reminderDate of its
     * corresponding poll
     *
     * @param pollID
     * @param reminderDate
     */
    public void createReminderTimeout(Long pollID, Date reminderDate) {
        TimerConfig tCfg = new TimerConfig();
        TimerInfo timerInfo = new TimerInfo(TimeoutCause.REMIND, pollID);
        tCfg.setInfo(timerInfo);
        timerService.createSingleActionTimer(reminderDate, tCfg);
    }

    /**
     * creates a timer which keeps track of the endDate of its corresponding
     * poll
     *
     * @param pollID
     * @param endDate
     */
    public void createVoteFinishedTimeout(Long pollID, Date endDate) {
        TimerConfig tCfg = new TimerConfig();
        TimerInfo timerInfo = new TimerInfo(TimeoutCause.END, pollID);
        tCfg.setInfo(timerInfo);
        timerService.createSingleActionTimer(endDate, tCfg);

    }

    /**
     * handler method which is called when the startDate of a vote has come Sets
     * the PollState to RUNNING, generates Tokens and sends out email
     * invitations
     *
     * @param pollID
     */
    public void handleVoteRunning(long pollID) {
        pollAccess.updateState(pollID, PollState.RUNNING);
        generateTokens(pollID);
        sendEmailInvitations(pollID);
    }

    /**
     * handler method which is called when the endDate of a vote has come
     * Invalidates all tokens which were not used, sends out a notification
     * email to the organizers and sets the PollState to FINISHED
     *
     * @param pollID
     */
    public void handleVoteFinished(long pollID) {
        tokenAccess.invalidateAllTokensForPoll(pollID);
        Poll poll = pollAccess.find(pollID);
        boolean success = checkIfVoteSuccessful(poll);
        if (success) {
            poll.setPollState(PollState.FINISHED);
            computeWinners(poll);
        } else {
            poll.setPollState(PollState.PROHIBITED);
        }
        treatMissingVotesAsAbstentions(poll);
        notifyOrganizers(poll, success);
        pollAccess.edit(poll);
    }

    /**
     * generates a unique token for each participant
     *
     * @param pollID
     */
    public void generateTokens(long pollID) {

        List<String> usedTokenValues = tokenAccess.getAllUsedTokens();
        Poll poll = pollAccess.find(pollID);

        for (Participant p : poll.getParticipants()) {
            Token t = new Token();
            t.setPoll(poll);
            String tokenValue = UUID.randomUUID().toString().replace("-", "");
            // assert uniqueness of token values
            while (usedTokenValues.contains(tokenValue)) {
                tokenValue = UUID.randomUUID().toString().replace("-", "");
            }
            usedTokenValues.add(tokenValue);
            t.setTokenValue(tokenValue);
            t.setValid(true);

            if (poll.isParticipationTracking()) {
                t.setParticipant(p);
                p.setToken(t);
            }
            poll.getTokens().add(t);
        }
        pollAccess.edit(poll);
    }

    /**
     * sends out email reminders containing the personal tokens to particpants
     * who have not voted yet If participation tracking was enabled for the
     * poll, only those participants are reminded which did not vote yet,
     * otherwise all participants are reminded and only one Email to all has to
     * be sent out.
     *
     * @param pollID
     */
    @Asynchronous
    public void sendEmailReminders(long pollID) {
        Poll poll = pollAccess.find(pollID);
        List<Participant> participants = poll.getParticipants();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

        try {
            if (!poll.isParticipationTracking()) {
                InternetAddress[] addressBcc = new InternetAddress[participants.size()];
                for (int i = 0; i < addressBcc.length; i++) {
                    addressBcc[i] = new InternetAddress(participants.get(i).getEmail());
                }

                Message msg = new MimeMessage(mailSession);
                msg.setSubject("[Votes Uni Koblenz] Reminder to vote for Poll \"" + poll.getTitle() + "\"");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.BCC, addressBcc);
                msg.setText("Dear Participant, \n\nThis mail serves to remind you to vote for poll \"" + poll.getTitle() + "\"."
                        + "The voting period started at :" + df.format(poll.getStartDate()) + " and will end at : " + df.format(poll.getEndDate()) + ".\n"
                        + "During the voting period you can submit your vote with your personal token. \n"
                        + "You can find your personal token in the invitation mail which was sent to you on " + df.format(poll.getStartDate()) + ".");
                Transport.send(msg);
            } else {
                for (Participant p : participants) {
                    if (!p.getHasVoted()) {
                        Message msg = new MimeMessage(mailSession);
                        msg.setSubject("[Votes Uni Koblenz] Reminder to vote for Poll \"" + poll.getTitle() + "\"");
                        msg.setSentDate(new Date());
                        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(p.getEmail(), false));
                        msg.setText("Dear Participant, \n\nThis mail serves to remind you to vote for poll \"" + poll.getTitle() + "\"."
                                + "The voting period started at " + df.format(poll.getStartDate()) + " and will end at " + df.format(poll.getEndDate()) + ".\n"
                                + "During the voting period you can submit your vote with your personal token : " + p.getToken() + "\n"
                                + "Follow this link to get to the poll : " + rootUrl + "/vote.xhtml?token=" + p.getToken());
                        Transport.send(msg);
                    }
                }
            }
        } catch (AddressException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * sends out email invitations to particpants. If participation tracking was
     * enabled for the poll tokens are linked with their participants. If
     * participation tracking was not enabled tokens are shuffled first to make
     * sure that DB admins can not infer token-participant relationships with
     * the help of ordering of the token and particpant result sets.
     *
     * @param pollID
     */
    @Asynchronous
    private void sendEmailInvitations(long pollID) {
        Poll poll = pollAccess.find(pollID);
        List<Token> tokens = poll.getTokens();
        Collections.shuffle(tokens);  // just to make sure that ordering can not be used to infer participant-token relationships

        for (int i = 0; i < poll.getParticipants().size(); i++) {
            Participant p = poll.getParticipants().get(i);
            try {
                Message msg = new MimeMessage(mailSession);
                msg.setSubject("[Votes Uni Koblenz] Invitation for Poll \"" + poll.getTitle() + "\"");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(p.getEmail(), false));
                String token;
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
                if (poll.isParticipationTracking()) {
                    token = p.getToken().getTokenValue();
                } else {
                    token = poll.getTokens().get(i).getTokenValue();
                }
                msg.setText("Dear Participant, \n\nYou were invited to vote for poll \"" + poll.getTitle() + "\"."
                        + " The voting period starts at " + df.format(poll.getStartDate()) + " and ends at " + df.format(poll.getEndDate()) + ".\n"
                        + "The poll includes " + poll.getParticipants().size() + " participants."
                        + " During the voting period you can submit your vote with your personal token : " + token + "\n"
                        + "Follow this link to get to the poll : " + rootUrl + "/vote.xhtml?token=" + token);
                Transport.send(msg);
            } catch (AddressException ex) {
                Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * sends out email notifications to the organizers of a poll when the poll
     * is finished
     *
     * @param poll
     */
    @Asynchronous
    private void notifyOrganizers(Poll poll, boolean success) {
        try {
            Message msg = new MimeMessage(mailSession);
            msg.setSubject("[Votes Uni Koblenz] Your Poll \"" + poll.getTitle() + "\" is finished!");
            msg.setSentDate(new Date());
            InternetAddress[] addressTo = new InternetAddress[poll.getOrganizers().size()];
            for (int i = 0; i < addressTo.length; i++) {
                addressTo[i] = new InternetAddress(poll.getOrganizers().get(i).getEmail());
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);
            if (success) {
                msg.setText("Dear Organizer(s), \n\n"
                        + "Your poll \"" + poll.getTitle() + "\" is finished now. If you want to publish the"
                        + " results go to " + rootUrl + "/organizer/overview.xhtml and click the publish link");
            } else {
                msg.setText("Dear Organizer(s), \n\n"
                        + "Your poll \"" + poll.getTitle() + "\" is finished now. Unfortunately"
                        + " the results are locked and can not be displayed because anonymity of the voters can not be assured.\n"
                        + "In order to restart the poll, go to " + rootUrl + "/organizer/overview.xhtml and click the restart link");
            }
            Transport.send(msg);
        } catch (AddressException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Asynchronous
    public void notifyParticipants(Poll poll) {

        List<Participant> participants = poll.getParticipants();
        try {
            InternetAddress[] addressBcc = new InternetAddress[participants.size()];
            for (int i = 0; i < addressBcc.length; i++) {
                addressBcc[i] = new InternetAddress(participants.get(i).getEmail());
            }

            Message msg = new MimeMessage(mailSession);
            msg.setSubject("[Votes Uni Koblenz] Results for Poll \"" + poll.getTitle() + "\"");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.BCC, addressBcc);
            msg.setText("Dear Participant, \n\nThe results for poll \"" + poll.getTitle() + "\" are available.\n"
                    + "Follow this link to get to the results page " + rootUrl + "/view.xhtml?pollid=" + poll.getMasterToken());
            Transport.send(msg);
        } catch (AddressException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PollLifecycleBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    /**
     * checks if anonymity of voters can be assured this is not the case when
     * participation tracking is enabled and one of the options of any item has
     * all the votes whereas all the others have 0 votes.
     *
     * @param poll
     * @return
     */
    public boolean checkIfVoteSuccessful(Poll poll) {
        if (!poll.isParticipationTracking()) {
            return true;
        } else {
            for (Item item : poll.getItems()) {
                ArrayList<ItemOption> options = new ArrayList<>(item.getOptions());
                Collections.sort(options, Collections.reverseOrder());
                // options are sorted in descending order according to their votes at this point that means if the option
                // with the second highest votes alreay has 0 votes then the winner has to have the rest of the votes
                if(options.get(1).getVotes() == 0 & options.get(0).getVotes() > 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * when the poll is finished the real number of abstentions is computed. The
     * number which was stored before is overriden but could also be stored in
     * the future in order to learn how many participants actually visited the
     * poll.
     *
     * @param poll
     */
    private void treatMissingVotesAsAbstentions(Poll poll) {
        for (Item item : poll.getItems()) {
            Integer votes = 0;
            for (ItemOption option : item.getOptions()) {
                votes += option.getVotes();
            }
            Integer realAbstentions = poll.getParticipants().size() - votes;
            item.setAbstentions(realAbstentions);
        }
    }

    /**
     * computes the winning options of a poll according to the following requirement : 
     * The decision mode of a poll can be one of three modes:
     * absolute majority: an option is accepted if more than 50% of the participants voted for that option
     * relative majority: an option is accepted if more than 50% of the votes indicate that option
     * simple majority: an option is accepted if it has more votes than all others
     * 
     * @param poll
     */
    private void computeWinners(Poll poll) {

        for (Item item : poll.getItems()) {
            if (item.getDecisionMode() == DecisionMode.ABS_MAJORITY) {

                ItemOption max = Collections.max(item.getOptions());

                if ((max.getVotes() / (double) poll.getParticipants().size()) >= 0.5) {
                    item.setWinner(max);
                    max.setWinningItem(item);
                }

            } else if (item.getDecisionMode() == DecisionMode.REL_MAJORITY) {
                Integer voteSum = 0;

                for (ItemOption option : item.getOptions()) {
                    voteSum += option.getVotes();
                }

                ItemOption max = Collections.max(item.getOptions());

                if ((max.getVotes() / (double) voteSum) >= 0.5) {
                    item.setWinner(max);
                    max.setWinningItem(item);
                }

            } else if (item.getDecisionMode() == DecisionMode.SIMPLE_MAJORITY) {
                List<ItemOption> options = new ArrayList<ItemOption>(item.getOptions());
                Collections.sort(options, Collections.reverseOrder());
                // do not set a winner if there are multiple winners
                ItemOption first = options.get(0);
                ItemOption second = options.get(1);
                
                if (first.getVotes() > second.getVotes()) {
                    item.setWinner(first);
                    first.setWinningItem(item);
                }
            }
        }
    }
}
