/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vote;

import Exceptions.VotesException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.PollTO;
import login.UserBean;

/**
 *
 * @author nico
 */
@Named(value = "voteBean")
@ViewScoped
public class VoteBean {

    @EJB
    VotesLogic votesLogic;
    @Inject
    UserBean userBean;

    private ResourceBundle messageBundle;

    private PollTO poll;
    private String token;

    private String ownOption = "";
    private String ownDescription = "";
    private FacesMessage facesMsg;

    /**
     * If the URL contains the request parameter token, load the poll linked to
     * this token.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");

        Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
        token = requestParameterMap.get("token");
        if (token != null) {
            try {
                poll = votesLogic.getPollByToken(token);
            } catch (VotesException ex) {
                Logger.getLogger(VoteBean.class.getName()).log(Level.SEVERE, null, ex);
                facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            }
        }
    }

    public void preRender() {
        if (facesMsg != null) {
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        }
    }

    public void addOwnOption(ItemTO item) {
        if (!"".equalsIgnoreCase(ownOption)) {
            item.createOwnOption(ownOption, ownDescription);
        }
        ownOption = "";
        ownDescription = "";
    }

    public void submitVote() {
        try {
            if (validateVote()) {
                votesLogic.submitVotes(poll.getItems(), token);
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext ec = context.getExternalContext();
                ec.getFlash().put("success", true);
                try {
                    ec.redirect(ec.getRequestContextPath());
                } catch (IOException ex) {
                    Logger.getLogger(VoteBean.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
        } catch (VotesException ex) {
            FacesContext context = FacesContext.getCurrentInstance();
            String message = messageBundle.getString(ex.getMessage());
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }
    }

    public void abstainFromVote() {
        for (ItemTO item : poll.getItems()) {
            item.setAbstainFlag(true);
        }
        try {
            votesLogic.submitVotes(poll.getItems(), token);
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.getFlash().put("success", true);
            try {
                ec.redirect(ec.getRequestContextPath());
            } catch (IOException ex) {
                Logger.getLogger(VoteBean.class.getName()).log(Level.SEVERE, ex.getMessage());
            }

        } catch (VotesException ex) {
            FacesContext context = FacesContext.getCurrentInstance();
            String message = messageBundle.getString(ex.getMessage());
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }

    }

    private boolean validateVote() {

        boolean valid = true;

        // Otherwise validate each item
        for (ItemTO item : poll.getItems()) {
            if (!item.getAbstainFlag()) {
                List<String> decisionList = new ArrayList<>();
                if (item.getDecision() != null
                        && !item.getDecision().isEmpty()) {
                    decisionList.add(item.getDecision());
                } else if (item.getDecisions() != null
                        && item.getDecisions().length > 0) {
                    decisionList.addAll(Arrays.asList(item.getDecisions()));
                }

                if (!decisionList.isEmpty()) {
                    if (decisionList.size() != item.getM()) {
                        FacesContext context = FacesContext.getCurrentInstance();
                        MessageFormat messageFormat = new MessageFormat(messageBundle.getString("vote.itemVoteIncomplete"));
                        Object[] args = {item.getM(), item.getTitle()};
                        String message = messageFormat.format(args);
                        FacesMessage msg = new FacesMessage(message, message);
                        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                        context.addMessage(null, msg);
                        valid = false;
                    } else {
                        for (String decision : decisionList) {
                            for (ItemOptionTO option : item.getOptions()) {
                                if (option.getShortName().equalsIgnoreCase(decision)) {
                                    option.setVoted(true);
                                    break;
                                }
                            }
                            for (ItemOptionTO option : item.getOwnOptions()) {
                                if (option.getShortName().equalsIgnoreCase(decision)) {
                                    option.setVoted(true);
                                    item.getOptions().add(option);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();
                    MessageFormat messageFormat = new MessageFormat(messageBundle.getString("vote.itemNotVoted"));
                    Object[] args = {item.getTitle()};
                    String message = messageFormat.format(args);
                    FacesMessage msg = new FacesMessage(message, message);
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(null, msg);
                    valid = false;
                }
            }
        }
        return valid;
    }

    /**
     * @return the poll
     */
    public PollTO getPoll() {
        return poll;
    }

    /**
     * @param poll the poll to set
     */
    public void setPoll(PollTO poll) {
        this.poll = poll;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getOwnOption() {
        return ownOption;
    }

    public void setOwnOption(String ownOption) {
        this.ownOption = ownOption;
    }

    public String getOwnDescription() {
        return ownDescription;
    }

    public void setOwnDescription(String ownDescription) {
        this.ownDescription = ownDescription;
    }

}
