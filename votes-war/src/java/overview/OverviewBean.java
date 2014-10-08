/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package overview;

import Exceptions.VotesException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.MailingListTO;
import logic.to.ParticipantTO;
import logic.to.PollTO;
import login.UserBean;
import persistence.entities.ItemType;

/**
 *
 * @author artur
 */
@Named(value = "overviewBean")
@ViewScoped
public class OverviewBean implements Serializable {

    @EJB
    private VotesLogic logic;
    @Inject
    private UserBean userBean;
    private List<PollTO> polls = new ArrayList<>();
    private String token;
    private PollTO selectedPoll;
    private ResourceBundle messageBundle;

    /**
     * Creates a new instance of OverviewBean
     */
    public OverviewBean() {
    }

    @PostConstruct
    public void init() {
        polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
        FacesContext context = FacesContext.getCurrentInstance();
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");

    }

    /**
     * Submit the given token and redirect to the vote.xhtml with the poll
     * linked to that token.
     *
     * @return the url to navigate to after submitting a token
     */
    public String submitToken() {
        return "/vote.xhtml?faces-redirect=true&amp;token=" + getToken();
    }

    /**
     * Returns a link to the edit poll page of the selected poll.
     *
     * @param pollId
     * @return the link to edit.xhtml with the selected poll.
     */
    public String editPoll(long pollId) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pollId", pollId);
        return "edit.xhtml?faces-redirect=true";
    }

    public String startPoll(long pollId) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pollId", pollId);
        return "start.xhtml?faces-redirect=true";
    }

    /**
     * Returns a link to the view poll page of the selected poll.
     *
     * @param pollId
     * @return the link to view.xhtml with the selected poll.
     */
    public String viewPoll(long pollId) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pollId", pollId);
        return "/view.xhtml?faces-redirect=true";
    }

    public String publishPoll(long pollId) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("pollId", pollId);
        try {
            logic.publishPoll(pollId);
        } catch (VotesException ex) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            // needed for ajax refresh
            polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
            return null;
        }
        return "overview.xhtml?faces-redirect=true";
    }

    public String exportParticipantList(Long pollID) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "cpts");
        String exportTitle = bundle.getString("poll.participantList");

        PollTO poll = logic.getPoll(pollID);
        
        if (poll==null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString("overview.pollDeleted"),"");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            // needed for ajax refresh
            polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
            return null;
        }

        List<ParticipantTO> participants = poll.getParticipants();
        MailingListTO mailingList = new MailingListTO();
        StringBuilder sb = new StringBuilder();

        for (ParticipantTO participant : participants) {
            sb.append(participant.getEmail()).append(";");
        }

        mailingList.setName(exportTitle + " " + poll.getTitle());
        mailingList.setMailAddresses(sb.toString());

        context.getExternalContext().getFlash().put("mailinglist", mailingList);
        return "mailingList.xhtml?faces-redirect=true";
    }

    public String restartPoll(Long pollID) {
        PollTO oldPoll = logic.getPoll(pollID);
        
        if (oldPoll==null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString("overview.pollDeleted"),"");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            // needed for ajax refresh
            polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
            return null;
        }

        PollTO newPoll = new PollTO();
        newPoll.setTitle(oldPoll.getTitle());
        newPoll.setCreator(oldPoll.getCreator());
        newPoll.setOrganizers(oldPoll.getOrganizers());
        newPoll.setDescription(oldPoll.getDescription());
        newPoll.setParticipants(oldPoll.getParticipants());
        newPoll.setParticipationTracking(oldPoll.isParticipationTracking());

        for (ItemTO item : oldPoll.getItems()) {
            ItemTO newItem = new ItemTO();
            newItem.setDecisionMode(item.getDecisionMode());
            newItem.setType(item.getType());
            if (item.getType() == ItemType.M_OF_N) {
                newItem.setM(item.getM());
            }
            newItem.getOptions().clear();
            newItem.setTitle(item.getTitle());
            for (ItemOptionTO option : item.getOptions()) {
                ItemOptionTO newOption = new ItemOptionTO();
                newOption.setShortName(option.getShortName());
                newOption.setDescription(option.getDescription());
                newItem.getOptions().add(newOption);
            }
            newPoll.getItems().add(newItem);
        }
        for (ParticipantTO participant : oldPoll.getParticipants()) {
            ParticipantTO newParticipant = new ParticipantTO();
            newParticipant.setEmail(participant.getEmail());
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("poll", newPoll);
        return "edit.xhtml?faces-redirect=true";
    }

    public String deletePoll(long pollId) {
        try {
            logic.deletePoll(pollId, false);
        } catch (VotesException ex) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            // needed for ajax refresh
            polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
            return null;
        }
        return "overview.xhtml?faces-redirect=true";
    }

    /**
     * returns a comma separated list of style classes which are used for table
     * rows in the overview.
     *
     * @return
     */
    public String getRowClasses() {
        if (getPolls().isEmpty()) {
            return "normal";
        } else {
            String rowClassString = "";
            for (PollTO poll : getPolls()) {
                rowClassString += poll.isProhibited() ? "danger," : "normal,";
            }
            return rowClassString.substring(0, rowClassString.length() - 1);
        }
    }
    
    public String extendVotingPeriod() {
        try {
            logic.extendVotingPeriod(selectedPoll.getId(), selectedPoll.getEnd());
        } catch (VotesException ex) {
            Logger.getLogger(OverviewBean.class.getName()).log(Level.SEVERE, ex.getMessage());
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            polls = logic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
            return null;
        }
        return "overview.xhtml?faces-redirect=true";
    }

    public List<PollTO> getPolls() {
        return polls;
    }

    public void setPolls(List<PollTO> polls) {
        this.polls = polls;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the selectedPoll
     */
    public PollTO getSelectedPoll() {
        return selectedPoll;
    }

    /**
     * @param poll 
     */
    public void setSelectedPoll(PollTO poll) {
        selectedPoll = new PollTO();
        selectedPoll.setId(poll.getId());
        selectedPoll.setStart(poll.getStart());
        selectedPoll.setEnd(poll.getEnd());
    }

}
