/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import Exceptions.VotesException;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import logic.VotesLogic;
import logic.to.OrganizerTO;

/**
 *
 * @author artur
 */
@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    @EJB
    VotesLogic votesLogic;

    private OrganizerTO organizer;
    private boolean inOrganizerRole;
    private boolean inAdminRole;
    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }

    public String switchLocale(String lang) {

        setLocale(Locale.forLanguageTag(lang));
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?faces-redirect=true";
    }

    /**
     * @return the inOrganizerRole
     */
    public boolean isInOrganizerRole() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        return ec.isUserInRole("ORGANIZER");
    }

    /**
     * @param isOrganizer the inOrganizerRole to set
     */
    public void setIsInOrganizerRole(boolean isOrganizer) {
        this.inOrganizerRole = isOrganizer;
    }

    /**
     * @return the inAdminRole
     */
    public boolean isInAdminRole() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        return ec.isUserInRole("ADMIN");
    }

    /**
     * @param isAdmin the inAdminRole to set
     */
    public void setIsInAdminRole(boolean isAdmin) {
        this.inAdminRole = isAdmin;
    }

    public OrganizerTO getOrganizer() {
        if (organizer == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Principal p = ec.getUserPrincipal();
            if (p != null) {
                String principalName = p.getName();
                try {
                    organizer = votesLogic.getOrganizerByUsername(principalName);
                } catch (VotesException ex) {
                    Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return organizer;
    }

    public void setOrganizer(OrganizerTO organizer) {
        this.organizer = organizer;
    }

    public void logout() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try {
            redirectToRoot();
        } catch (IOException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            organizer = null;
            FacesContext.getCurrentInstance().responseComplete();
            req.getSession(false).invalidate();
        }
    }

    /**
     * redirects to the application root. Organizers will be redirected to
     * overview and users which are not logged in will get back to the login
     * page
     *
     * @throws IOException
     */
    public void redirectToRoot() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath());
    }


    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
