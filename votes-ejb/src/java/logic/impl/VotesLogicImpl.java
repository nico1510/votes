package logic.impl;

import Exceptions.VotesException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.persistence.LockModeType;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.MailingListTO;
import logic.to.OrganizerTO;
import logic.to.ParticipantTO;
import logic.to.PollTO;
import persistence.ItemAccess;
import persistence.ItemOptionAccess;
import persistence.MailingListAccess;
import persistence.OrganizerAccess;
import persistence.ParticipantAccess;
import persistence.PollAccess;
import persistence.TokenAccess;
import persistence.entities.Item;
import persistence.entities.ItemOption;
import persistence.entities.MailingList;
import persistence.entities.Organizer;
import persistence.entities.Participant;
import persistence.entities.Poll;
import persistence.entities.PollState;
import persistence.entities.Token;
import util.TimerInfo.TimeoutCause;

/**
 * Session Bean implementation class VoteTest
 */
@Stateless
@DeclareRoles({"ADMIN", "ORGANIZER"})
public class VotesLogicImpl implements VotesLogic {

    @EJB
    PollAccess pollAccess;
    @EJB
    OrganizerAccess organizerAccess;
    @EJB
    ItemOptionAccess itemOptionAccess;
    @EJB
    ItemAccess itemAccess;
    @EJB
    ParticipantAccess participantAccess;
    @EJB
    TokenAccess tokenAccess;
    @EJB
    MailingListAccess mailingListAccess;
    @EJB
    PollLifecycleBean pollLifecycleBean;
    @EJB
    TestDataGenerator testDataGenerator;

    /**
     * Default constructor.
     */
    public VotesLogicImpl() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void generatePreparedTestPoll() {
        testDataGenerator.generatePreparedPoll();
    }

    @Override
    public void generateFinishedTestPoll() {
        testDataGenerator.generateFinishedPoll();
    }

    @Override
    public void generateProhibitedTestPoll() {
        testDataGenerator.generateProhibitedPoll();
    }

    @Override
    public void generateStartedTestPoll() {
        testDataGenerator.generateStartedPoll();
    }

    @Override
    public void generatePublishedTestPoll() {
        testDataGenerator.generatePublishedPoll();
    }

    /**
     * returns a list of all polls where a specific user is the organizer
     *
     * @param organizerID
     * @return
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public List<PollTO> getAllPollsForOrganizer(long organizerID) {
        return Poll.createTransferList(pollAccess.getPollAllPollsForOrganizer(organizerID));
    }
    
    /**
     * returns a list of all mailinglists for a specific organizer
     *
     * @param organizerID
     * @return
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public List<MailingListTO> getMailingListsForOrganizer(long organizerID) {
        return MailingList.createTransferList(organizerAccess.find(organizerID).getMailinglists());
    }

    /**
     * saves or edits a poll
     *
     * @param pollTO
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public void savePoll(PollTO pollTO) throws VotesException {

        Poll poll;
        if (pollTO.getId() == null) {
            poll = new Poll();
            poll.setPollState(PollState.PREPARED);
        } else {
            poll = pollAccess.find(pollTO.getId());
            
            if (poll==null) {
                throw new VotesException("overview.pollDeleted");
            }
            if (poll.getPollState() != PollState.PREPARED) {
                throw new VotesException("overview.oldView");
            }
            // since we use transfer objects we can't use optimistic locking as is but have
            // to pass around the version number and check at this point if it is still the same
            if (poll.getVersionId() != pollTO.getVersionId()) {
                throw new VotesException("preview.modificationError");
            }
        }
        poll.setTitle(pollTO.getTitle());
        poll.setDescription(pollTO.getDescription());
        poll.setStartDate(pollTO.getStart());
        poll.setEndDate(pollTO.getEnd());
        poll.setReminderDate(pollTO.getReminderDate());
        poll.setParticipationTracking(pollTO.isParticipationTracking());

        Organizer creator = organizerAccess.find(pollTO.getCreator().getId());
        creator.getCreatedPolls().add(poll);
        poll.setCreator(creator);

        List<Organizer> organizers = poll.getOrganizers();

        for (Organizer organizer : organizers) {
            organizer.getPolls().remove(poll);
        }

        organizers.clear();

        for (OrganizerTO organizerTO : pollTO.getOrganizers()) {
            Organizer organizer = organizerAccess.find(organizerTO.getId());
            organizer.getPolls().add(poll);
            organizers.add(organizer);
        }

        List<Item> items = poll.getItems();
        items.clear();

        for (ItemTO itemTO : pollTO.getItems()) {
            Item item = (itemTO.getId() == null) ? new Item() : itemAccess.find(itemTO.getId());
            item.setM(itemTO.getM());
            item.setPoll(poll);
            item.setTitle(itemTO.getTitle());
            item.setType(itemTO.getType());
            item.setDecisionMode(itemTO.getDecisionMode());

            List<ItemOption> options = item.getOptions();
            options.clear();

            for (ItemOptionTO itemOptionTO : itemTO.getOptions()) {
                ItemOption itemOption = (itemOptionTO.getId() == null) ? new ItemOption() : itemOptionAccess.find(itemOptionTO.getId());
                itemOption.setDescription(itemOptionTO.getDescription());
                itemOption.setShortName(itemOptionTO.getShortName());
                itemOption.setItem(item);
                options.add(itemOption);
            }

            items.add(item);
        }

        List<Participant> participants = poll.getParticipants();
        participants.clear();

        for (ParticipantTO participantTO : pollTO.getParticipants()) {
            Participant participant = (participantTO.getId() == null) ? new Participant() : participantAccess.find(participantTO.getId());
            participant.setEmail(participantTO.getEmail());
            participant.setPoll(poll);
            participant.setHasVoted(false);
            participants.add(participant);
        }

        if (pollTO.getId() == null) {
            pollAccess.create(poll);
        } else {
            pollAccess.edit(poll);
        }
    }

    /**
     * increases the vote count for every option which is marked as voted and
     * increases the abstentions count for every item which is marked as such.
     * In the end the token is invalidated and it is checked if this vote was
     * the last remaining vote so that the poll is finished.
     *
     * @param items
     * @param tokenValue
     * @throws Exceptions.VotesException
     */
    @Override
    public void submitVotes(List<ItemTO> items, String tokenValue) throws VotesException {

        Token token = tokenAccess.findByTokenValue(tokenValue);
        if (token == null) {
            throw new VotesException("vote.invalidPoll");
        } else if (!token.isValid()) {
            throw new VotesException("vote.tokenUsed");
        } else {
            long pollID = token.getPoll().getId();

            for (ItemTO itemTO : items) {
                if (itemTO.getAbstainFlag()) {
                    itemAccess.increaseAbstentions(itemTO.getId());
                } else {
                    for (ItemOptionTO optionTO : itemTO.getOptions()) {

                        // this is the case when a user added an own option
                        if (optionTO.getId() == null) {
                            ItemOption newOption = new ItemOption();
                            newOption.setShortName(optionTO.getShortName());
                            newOption.setDescription(optionTO.getDescription());
                            if (optionTO.isVoted()) {
                                newOption.setVotes(1);
                            }
                            Item item = itemAccess.getEntityManager().find(Item.class, itemTO.getId(), LockModeType.PESSIMISTIC_WRITE);
                            newOption.setItem(item);
                            item.getOptions().add(newOption);
                            itemAccess.edit(item);
                            
                        } else {
                            if (optionTO.isVoted()) {
                                itemOptionAccess.voteForOption(optionTO.getId());
                            }
                        }
                    }
                }
            }
            token.setValid(false);
            tokenAccess.edit(token);
            Participant participant = token.getParticipant();

            // if participant is not null then participation tracking was enabled
            if (participant != null) {
                participant.setHasVoted(true);
                participantAccess.edit(participant);
            }
            checkIfVoteAlreadyFinished(pollID);
        }
    }

    /**
     * looks up uniko users via ldap
     *
     * @param uid
     * @return
     */
    private OrganizerTO lookUpOrganizer(String uid) {

        OrganizerTO organizer = new OrganizerTO();

        Hashtable<String, String> env = new Hashtable<>();

        String sp = "com.sun.jndi.ldap.LdapCtxFactory";
        env.put(Context.INITIAL_CONTEXT_FACTORY, sp);

        String ldapUrl = "ldaps://ldap.uni-koblenz.de";
        env.put(Context.PROVIDER_URL, ldapUrl);

        DirContext dctx = null;
        try {
            dctx = new InitialDirContext(env);
            String base = "ou=people,ou=koblenz,dc=uni-koblenz-landau,dc=de";

            SearchControls sc = new SearchControls();
            String[] attributeFilter = {"uid", "sn", "givenname"};
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = "(&(objectClass=Person)(uid=" + uid + "))";
            NamingEnumeration results = dctx.search(base, filter, sc);
            if (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
                Attribute a = attrs.get("uid");
                if (a != null) {
                    organizer.setUsername((String) a.get());
                    organizer.setEmail((String) a.get() + "@uni-koblenz.de");
                }
                a = attrs.get("givenname");
                if (a != null) {
                    organizer.setRealname((String) a.get());
                }
                a = attrs.get("sn");
                if (a != null) {
                    organizer.setRealname(organizer.getRealname() + " " + (String) a.get());
                }
            } else {
                organizer = null;
            }
        } catch (NamingException ex) {
            organizer = null;
            Logger.getLogger(VotesLogicImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (dctx != null) {
                try {
                    dctx.close();
                } catch (NamingException ex) {
                    Logger.getLogger(VotesLogicImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return organizer;
    }

    /**
     * returns a poll by its id
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public PollTO getPoll(long id) {
        Poll poll = pollAccess.find(id);
        return (poll==null)? null : poll.createTO();
    }

    /**
     * uses the username to check if the user is already in the database if not
     * the user is persisted
     *
     * @param username
     * @return
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public OrganizerTO getOrganizerByUsername(String username) throws VotesException {
        Organizer organizer = organizerAccess.findByUsername(username);
        if (organizer == null) {
            // Organizer is logged in for the first time
            // -> look him up on ldap and persist his information into DB
            OrganizerTO organizerTO = lookUpOrganizer(username);
            if (organizerTO == null) {
                throw new VotesException("organizer.notFound");
            }
            organizer = new Organizer();
            organizer.setEmail(organizerTO.getEmail());
            organizer.setRealname(organizerTO.getRealname());
            organizer.setUsername(organizerTO.getUsername());
            organizerAccess.create(organizer);
        }
        return organizer.createTO();
    }

    /**
     * uses the username to check if the user is known
     *
     * @param username
     * @return
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public boolean existsOrganizer(String username) {
        Organizer organizer = organizerAccess.findByUsername(username);
        if (organizer == null) {
            OrganizerTO organizerTO = lookUpOrganizer(username);
            return (organizerTO != null);
        }
        return true;
    }

    /**
     * returns the poll for a specific token
     *
     * @param tokenValue
     * @return
     * @throws Exceptions.VotesException
     */
    @Override
    public PollTO getPollByToken(String tokenValue) throws VotesException {
        Poll poll = pollAccess.getPollByToken(tokenValue);
        if (poll != null) {
            Token token = tokenAccess.findByTokenValue(tokenValue);
            if(token.isValid()) {
                return poll.createTO();
            } else {
                throw new VotesException("vote.tokenUsed");
            }
        } else {
            throw new VotesException("vote.invalidPoll");
        }
    }

    /**
     * starts a poll and creates timers for the PollState changes
     *
     * @param pollID
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public void startPoll(long pollID) throws VotesException{
        Poll poll = pollAccess.find(pollID);
        if (poll==null) {
            throw new VotesException("overview.pollDeleted");
        }
        if (poll.getPollState() != PollState.PREPARED || poll.getStartDate().getTime() < System.currentTimeMillis()) {
            throw new VotesException("overview.oldView");
        }

        pollAccess.updateState(pollID, PollState.STARTED);

        if (poll.getReminderDate() != null) {
            pollLifecycleBean.createReminderTimeout(poll.getId(), poll.getReminderDate());
        }
        pollLifecycleBean.createVoteStartedTimeout(poll.getId(), poll.getStartDate());
        pollLifecycleBean.createVoteFinishedTimeout(poll.getId(), poll.getEndDate());

    }

    /**
     * checks if all participants have submitted their votes. If so the
     * remaining timers are cancelled and the vote fnished handler is executed
     *
     *
     * @param pollID
     */
    private void checkIfVoteAlreadyFinished(long pollID) {
        if (pollAccess.getRemainingVotesCount(pollID) == 0) {
            // cancel timer which is fired at end date
            pollLifecycleBean.cancelTimer(pollID, TimeoutCause.END);
            // if there was a timer for sending email reminders it is not needed anymore now
            pollLifecycleBean.cancelTimer(pollID, TimeoutCause.REMIND);
            pollLifecycleBean.handleVoteFinished(pollID);
        }
    }

    /**
     * extends the voting period of the poll by changing the end date and
     * creating a new timer for the updated end date
     *
     * @param pollID
     * @param newEndDate
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public void extendVotingPeriod(long pollID, Date newEndDate) throws VotesException {
        Poll poll = pollAccess.find(pollID);
        if (poll==null) {
            throw new VotesException("overview.pollDeleted");
        }
        if (!(poll.getEndDate().getTime() < newEndDate.getTime())) {
            throw new VotesException("overview.extendPeriodDateError");
        }
        if (!(poll.getPollState() == PollState.STARTED || poll.getPollState() == PollState.RUNNING)) {
            throw new VotesException("overview.oldView");
        }
        poll.setEndDate(newEndDate);
        pollAccess.edit(poll);
        pollLifecycleBean.cancelTimer(pollID, TimeoutCause.END);
        pollLifecycleBean.createVoteFinishedTimeout(pollID, newEndDate);
    }

    /**
     * deletes a poll and cancels all timers which are possibly still associated
     * with it
     *
     * @param pollID
     * @param asadmin
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed({"ADMIN", "ORGANIZER"})
    public void deletePoll(long pollID, boolean asadmin) throws VotesException {
        Poll poll = pollAccess.find(pollID);
        if (poll==null) {
            throw new VotesException("overview.pollDeleted");
        }
        if(!asadmin) {
            if (poll.getPollState() == PollState.RUNNING || poll.getPollState() == PollState.STARTED  || poll.getPollState() == PollState.PUBLISHED) {
                throw new VotesException("overview.oldView");
            }
        }
        pollAccess.remove(poll);

        for (TimeoutCause cause : new TimeoutCause[]{TimeoutCause.START, TimeoutCause.REMIND, TimeoutCause.END}) {
            pollLifecycleBean.cancelTimer(pollID, cause);
        }
    }

    /**
     * updates the mailing lists for an organizer
     *
     * @param organizerId
     * @param mailinglists
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public void updateMailingListsForOrganizer(long organizerId, List<MailingListTO> mailinglists) {
        Organizer organizer = organizerAccess.find(organizerId);

        List<MailingList> mailingLists = organizer.getMailinglists();
        mailingLists.clear();

        for (MailingListTO mailinglistTO : mailinglists) {
            MailingList mailingList = (mailinglistTO.getId() == null) ? new MailingList() : mailingListAccess.find(mailinglistTO.getId());
            mailingList.setName(mailinglistTO.getName());
            mailingList.setMailAdresses(mailinglistTO.getMailAddresses());
            mailingList.setOrganizer(organizer);
            mailingLists.add(mailingList);
        }

        organizerAccess.edit(organizer);
    }

    /**
     * sets the poll state to published if it was in state finished before
     *
     * @param pollID
     * @throws Exceptions.VotesException
     */
    @Override
    @RolesAllowed("ORGANIZER")
    public void publishPoll(long pollID) throws VotesException {
        Poll p = pollAccess.find(pollID);
        if (p==null) {
            throw new VotesException("overview.pollDeleted");
        }
        if (p.getPollState() == PollState.FINISHED) {
            p.setPollState(PollState.PUBLISHED);
            String masterToken = UUID.randomUUID().toString().replace("-", "");
            // generate until unique masterToken is found
            while(pollAccess.getPollByMasterToken(masterToken) != null) {
                masterToken = UUID.randomUUID().toString().replace("-", "");
            }
            p.setMasterToken(masterToken);
            pollLifecycleBean.notifyParticipants(p);
            pollAccess.edit(p);
        }
    }

    /**
     * returns the poll based on its masterToken The masterToken is used to
     * publish the poll to all participants but without using the actual
     * database id of the poll
     *
     * @param masterToken
     * @return
     * @throws Exceptions.VotesException
     */
    @Override
    public PollTO getPollByMasterToken(String masterToken) throws VotesException {
        Poll poll = pollAccess.getPollByMasterToken(masterToken);
        if (poll != null) {
            return poll.createTO();
        } else {
            throw new VotesException("view.invalidPollId");
        }
    }

    @Override
    public boolean existsPollTitle(String title, Long pollId) {
        List<Poll> allPolls = pollAccess.findAll();
        for (Poll poll : allPolls) {
            if (poll.getTitle().trim().equalsIgnoreCase(title.trim()) && !poll.getId().equals(pollId)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    @RolesAllowed("ADMIN")
    public List<PollTO> getAllPolls() {
        List<Poll> allPolls = pollAccess.findAll();
        if (allPolls != null) {
            return Poll.createTransferList(allPolls);
        } else {
            return null;
        }
    }

}
