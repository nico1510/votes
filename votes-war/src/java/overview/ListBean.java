/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package overview;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.MailingListTO;
import login.UserBean;

/**
 *
 * @author artur
 */
@Named(value = "listBean")
@ViewScoped
public class ListBean {

    @EJB
    private VotesLogic logic;
    @Inject
    private UserBean userBean;

    private ResourceBundle messageBundle;

    private List<MailingListTO> mailingLists = new ArrayList<>();
    private MailingListTO selectedList;
    private String listName = "";
    private boolean editMode = false;

    /**
     * Creates a new instance of ListBean
     */
    public ListBean() {

    }

    @PostConstruct
    private void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        Flash flash = context.getExternalContext().getFlash();

        // load resource bundles
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");

        // we need a copy of the list because the mailinglist-list of the organizer is sessionscoped
        // and this bean is viewscoped and we want to be able to change the list in multiple views
        mailingLists = logic.getMailingListsForOrganizer(userBean.getOrganizer().getId());

        //if the export mailinglist button was clicked in overview, the flash contains an exported list
        if (flash.containsKey("mailinglist")) {
            selectedList = (MailingListTO) flash.get("mailinglist");
            setEditMode(true); // user can change the exported list e.g its name
        }  else if (flash.containsKey("mailinglistName")) {
            selectedList = getListByName((String) flash.get("mailinglistName"));
        }
        else {
            selectedList = (mailingLists.size() > 0) ? mailingLists.get(0) : null;
        }
    }

    public void createList() {
        MailingListTO newList = new MailingListTO();
        newList.setName(listName);
        selectedList = newList;
        goInEditMode();
        listName = "";
    }

    public void removeSelectedList() {
        if (selectedList != null) {
            mailingLists.remove(selectedList);
            logic.updateMailingListsForOrganizer(userBean.getOrganizer().getId(), mailingLists);
            selectedList = (mailingLists.size() > 0) ? mailingLists.get(0) : null;
        }
    }

    public void listChanged(ValueChangeEvent event) {
        String newlistName = (String) event.getNewValue();
        selectedList = getListByName(newlistName);
    }

    public String editParticipants() {
        if (validateListName(selectedList)) {
            setEditMode(false);
            
            if(!mailingLists.contains(selectedList)){
                mailingLists.add(selectedList);
            }
            logic.updateMailingListsForOrganizer(userBean.getOrganizer().getId(), mailingLists);
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("mailinglistName", selectedList.getName());
            return "mailingList.xhtml?faces-redirect=true";
        } else {
            return null;
        }
    }

    public String cancelEdits() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("mailinglistName", selectedList.getName());
        return "mailingList.xhtml?faces-redirect=true";
    }

    public void goInEditMode() {
        setEditMode(true);
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public MailingListTO getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(MailingListTO selectedList) {
        this.selectedList = selectedList;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<MailingListTO> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(List<MailingListTO> mailingLists) {
        this.mailingLists = mailingLists;
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean validateListName(MailingListTO changedMailinglist) {
        if (changedMailinglist.getName().trim().isEmpty()) {
            String message = messageBundle.getString("mailinglist.empty");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, msg);
            return false;
        }

        for (MailingListTO mailingList : mailingLists) {
            if (mailingList.getName().equals(changedMailinglist.getName())
                    // this is the case when the new mailinglist has the same name than another one which is already persisted
                    && ((changedMailinglist.getId() == null)
                    // this is the case when 2 malinglists have the same name which are both persisted
                    || !mailingList.getId().equals(changedMailinglist.getId()))) {

                String message = messageBundle.getString("mailinglist.duplicate");
                FacesMessage msg = new FacesMessage(message, message);
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, msg);
                return false;
            }
        }
        return true;
    }

    private MailingListTO getListByName(String mailinglistName) {
        for (MailingListTO mailinglist : mailingLists) {
            if (mailinglist.getName().equals(mailinglistName)) {
                return mailinglist;
            }
        }
        return null;
    }

}
