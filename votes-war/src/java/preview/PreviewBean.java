/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preview;

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
@Named(value = "previewBean")
@ViewScoped
public class PreviewBean {

    @EJB
    private VotesLogic logic;
    private PollTO poll;
    private ResourceBundle messageBundle;
    private boolean participantPanelShowMore = false;
    private boolean organizerPanelShowMore = false;

    /**
     * Creates a new instance of PreviewBean
     */
    public PreviewBean() {
    }

    /**
     * Load given poll from flash and init form.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        Flash flash = context.getExternalContext().getFlash();
        // load poll from flash
        if (flash.containsKey("poll")) {
            poll = (PollTO) flash.get("poll");
        }

        // load resource bundles
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");
    }

    public void toggleParticipantPanel() {
        participantPanelShowMore = !participantPanelShowMore;
    }

    public void toogleOrganizerPanel() {
        organizerPanelShowMore = !organizerPanelShowMore;
    }

    public String createPoll() {
        try {
            logic.savePoll(poll);
        } catch (VotesException ex) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            return null;
        }
        return "overview.xhtml?faces-redirect=true";
    }

    public String editPoll() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("poll", poll);
        return "edit.xhtml?faces-redirect=true";
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
