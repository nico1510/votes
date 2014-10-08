/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edit;

import Exceptions.VotesException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.MailingListTO;
import logic.to.OrganizerTO;
import logic.to.ParticipantTO;
import logic.to.PollTO;
import login.UserBean;
import persistence.entities.DecisionMode;
import persistence.entities.ItemType;

/**
 *
 * @author artur
 */
@Named(value = "editBean")
@ViewScoped
public class EditBean {

    @EJB
    private VotesLogic logic;
    @Inject
    private UserBean userBean;

    private PollTO poll;
    private ResourceBundle messageBundle;
    private ResourceBundle captionsBundle;

    private String newOrganizer;
    private String newParticipant;

    private boolean organizerPanelShowMore = true;
    private boolean participantPanelShowMore = true;
    private boolean pollItemPanelShowMore = true;
    
    
    private List<MailingListTO> mailingLists = new ArrayList<>();
    private MailingListTO selectedList;

    /**
     * Creates a new instance of editBean
     */
    public EditBean() {
    }

    /**
     * If editBean is called and a pollId is given load the poll for this id. If
     * a poll object is given use this poll object. Otherwise create a new poll.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        Flash flash = context.getExternalContext().getFlash();

        // load resource bundles
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");
        captionsBundle = context.getApplication().getResourceBundle(context, "cpts");
        
        mailingLists = logic.getMailingListsForOrganizer(userBean.getOrganizer().getId());
        if (mailingLists.size() > 0) {
            selectedList = mailingLists.get(0);
        }
        
        // if poll is null create a new poll
        if (poll == null) {
            poll = new PollTO();
            poll.setCreator(getUserBean().getOrganizer());
            poll.createItem();
            poll.getItems().get(0).getOptions().get(0).setShortName(captionsBundle.getString("yes"));
            poll.getItems().get(0).getOptions().get(1).setShortName(captionsBundle.getString("no"));
        }
        
        // if flash contained a poll then overwrite the new poll
        if (flash.containsKey("poll")) {
            poll = (PollTO) flash.get("poll");
        } // or if flash contained a pollId overwrite the new poll
        else if (flash.containsKey("pollId")) {
            Long pollId = (Long) flash.get("pollId");
            poll = logic.getPoll(pollId);
        }
        
        // if poll is now null again then the polls in flash was deleted in the meantime
        if (poll==null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString("overview.pollDeleted"), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        }
    }
    

    public void itemTypeChanged(ValueChangeEvent event) {
        UIComponent component = event.getComponent();
        ItemTO item = (ItemTO) component.getAttributes().get("item");
        ItemType type =(ItemType)event.getNewValue();
        if (type != ItemType.M_OF_N) {
            item.setM(1);
        }
        if (type == ItemType.YES_NO) {
            while (item.getOptions().size() > 2) {
                item.getOptions().remove(item.getOptions().size() - 1);
            }
            item.getOptions().get(0).setShortName(captionsBundle.getString("yes"));
            item.getOptions().get(1).setShortName(captionsBundle.getString("no"));
        }
    }
    
    public void selectParticipantList(MailingListTO mailingList) {
                selectedList = mailingList;
    }

    /**
     * Checks if email in organizer input is correct. If email is correct, adds
     * new organizer to the list of organizers, otherwise sents error message.
     */
    public void addOrganizer() {
        try {
            if (newOrganizer.contains("@")) {
                String[] split = newOrganizer.split("@");
                newOrganizer = split[0];
            }
            OrganizerTO organizer = logic.getOrganizerByUsername(newOrganizer);
            poll.addOrganizer(organizer);
            newOrganizer = "";
        } catch (VotesException ex) {
            Logger.getLogger(EditBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addParticipant() {
        if (!newParticipant.isEmpty()) {
            ParticipantTO participant = new ParticipantTO();
            participant.setEmail(newParticipant);
            poll.addParticipant(participant);
            newParticipant = "";
        }
    }
    
    public void resetParticipants() {
        poll.getParticipants().clear();
    }
    
    
    public void addParticipantList() {
        for (String address : selectedList.getMailAdressList()) {
            ParticipantTO participant = new ParticipantTO();
            participant.setEmail(address);
            if (!poll.getParticipants().contains(participant)) {
                poll.addParticipant(participant);
            }
        }
    }

    public String previewPoll() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getMessageList().isEmpty()) {
            context.getExternalContext().getFlash().put("poll", poll);
            return "preview.xhtml?faces-redirect=true";
        }
        else {
            return null;
        }
    }
        
    public void validateForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        // Check if at least 3 participants
        if (poll.getParticipants().size() < 3) {
            String message = messageBundle.getString("poll.participants.minError");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }
        
        // Check if all items are properly filled out
        if (!arePollElementsComplete()){
            String message = messageBundle.getString("poll.incompletePoll");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }
        
        // Check if all item titles are unique
        if (!arePollElementsUnique()) {
            System.out.println("Duplicate poll items");
            String message = messageBundle.getString("poll.duplicatePollItem");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, msg);
        }
        
        if (poll.getStart() != null && poll.getEnd() != null) {
            // Check if start date is before end date
            if (!poll.getStart().before(poll.getEnd())) {
                String message = messageBundle.getString("poll.endBeforeStart");
                FacesMessage msg = new FacesMessage(message, message);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage(null, msg);
            }

            // Check if start date is in the future
            if (poll.getStart().before(new Date())) {
                String message = messageBundle.getString("poll.startInPast");
                FacesMessage msg = new FacesMessage(message, message);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage(null, msg);
            }

            // Check if reminder is between start date and end date
            if (poll.getReminderDate() != null) {
                if (poll.getReminderDate().before(poll.getStart()) || poll.getReminderDate().after(poll.getEnd())) {
                    String message = messageBundle.getString("poll.reminderNotInPollDuration");
                    FacesMessage msg = new FacesMessage(message, message);
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(null, msg);
                }
            }
        } else {
            if (poll.getStart() == null) {
                    String message = messageBundle.getString("poll.noStartDate");
                    FacesMessage msg = new FacesMessage(message, message);
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(null, msg);
            } 
            if (poll.getEnd()== null) {
                    String message = messageBundle.getString("poll.noEndDate");
                    FacesMessage msg = new FacesMessage(message, message);
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(null, msg);
            }
        }
    }
    
    private boolean arePollElementsComplete() {
        for (ItemTO item : poll.getItems()) {
            if (item.getTitle() == null || item.getTitle().isEmpty()) return false;
            for(ItemOptionTO option : item.getOptions()) {
                if (option.getShortName() == null || option.getShortName().isEmpty()) return false;
            }
        }
        return true;
    }
    
    private boolean arePollElementsUnique() {
        HashSet<String> titles = new HashSet<>();
        for (ItemTO item : poll.getItems()) {
            if (item.getTitle()!= null){
                titles.add(item.getTitle());
            }
        }
        return titles.size() == poll.getItems().size();
    }

    public void toggleParticipantPanel() {
        participantPanelShowMore = !participantPanelShowMore;
    }

    public void toggleOrganizerPanel() {
        organizerPanelShowMore = !organizerPanelShowMore;
    }

    public void togglePollItemPanel() {
        pollItemPanelShowMore = !pollItemPanelShowMore;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getNewOrganizer() {
        return newOrganizer;
    }

    public void setNewOrganizer(String newOrganizer) {
        this.newOrganizer = newOrganizer;
    }

    public String getNewParticipant() {
        return newParticipant;
    }

    public void setNewParticipant(String newParticipant) {
        this.newParticipant = newParticipant;
    }

    public boolean isOrganizerPanelShowMore() {
        return organizerPanelShowMore;
    }

    public void setOrganizerPanelShowMore(boolean organizerPanelShowMore) {
        this.organizerPanelShowMore = organizerPanelShowMore;
    }

    public boolean isParticipantPanelShowMore() {
        return participantPanelShowMore;
    }

    public void setParticipantPanelShowMore(boolean participantPanelShowMore) {
        this.participantPanelShowMore = participantPanelShowMore;
    }

    public boolean isPollItemPanelShowMore() {
        return pollItemPanelShowMore;
    }

    public void setPollItemPanelShowMore(boolean pollItemPanelShowMore) {
        this.pollItemPanelShowMore = pollItemPanelShowMore;
    }

    public PollTO getPoll() {
        return poll;
    }

    public void setPoll(PollTO poll) {
        this.poll = poll;
    }

    public ItemType[] getItemTypes() {
        return ItemType.values();
    }

    public DecisionMode[] getDecisionModi() {
        return DecisionMode.values();
    }

    public List<MailingListTO> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(List<MailingListTO> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public MailingListTO getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(MailingListTO selectedList) {
        this.selectedList = selectedList;
    }
    
    /**
     * reminder has to be at least one minute before poll end
     * 
     * @return
     */
    public Date getMaxReminderDate() {
        if(poll.getEnd() != null) {
            return new Date(poll.getEnd().getTime()-60000);
        } else {
            return null;
        }
    }
    
    /**
     * reminder has to be at least 1 minute after poll start
     * 
     * @return
     */
    public Date getMinReminderDate() {
        if(poll.getStart()!=null) {
            return new Date(poll.getStart().getTime()+60000);
        } else {
            return null;
        }
    }
}
