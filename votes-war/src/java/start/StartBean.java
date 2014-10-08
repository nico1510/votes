/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package start;

import Exceptions.VotesException;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.PollTO;

/**
 *
 * @author artur
 */
@Named(value = "startBean")
@ViewScoped
public class StartBean {

    @EJB
    private VotesLogic logic;
    private PollTO poll;
    private boolean participantPanelShowMore = false;
    private boolean organizerPanelShowMore = false;
    private ResourceBundle messageBundle;
    
    /**
     * Creates a new instance of PreviewBean
     */
    public StartBean() {
    }
    
    /**
     * Load given poll from flash and init form.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        // load poll with given poll id
        if (flash.containsKey("pollId")) {
            Long pollId = (Long) flash.get("pollId");
            poll = logic.getPoll(pollId);
        }
        if (poll==null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString("overview.oldView"),"");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        }
    }
    
    public void toggleParticipantPanel() {
        participantPanelShowMore = !participantPanelShowMore;
    }
    
    public void toogleOrganizerPanel() {
        organizerPanelShowMore = !organizerPanelShowMore;
    }
    
    public String startPoll() {
        try {
            logic.startPoll(poll.getId());
        } catch (VotesException ex) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            return null;
        }
        return "overview.xhtml?faces-redirect=true";
    }
    
    public PollTO getPoll() {
        return poll;
    }

    public void setPoll(PollTO poll) {
        this.poll = poll;
    }

    public boolean isParticipantPanelShowMore() {
        return participantPanelShowMore;
    }

    public void setParticipantPanelShowMore(boolean participantPanelShowMore) {
        this.participantPanelShowMore = participantPanelShowMore;
    }

    public boolean isOrganizerPanelShowMore() {
        return organizerPanelShowMore;
    }

    public void setOrganizerPanelShowMore(boolean organizerPanelShowMore) {
        this.organizerPanelShowMore = organizerPanelShowMore;
    }
}
