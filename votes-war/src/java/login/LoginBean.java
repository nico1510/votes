/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package login;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author artur
 */
@Named(value = "loginBean")
@ViewScoped
public class LoginBean implements Serializable {
    
    private String token = "";
    private boolean ldapLogin = false;

    /**
     * Creates a new instance of loginBean
     */
    public LoginBean() {
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isLdapLogin() {
        return ldapLogin;
    }
    
    public void setLdapLogin(boolean ldapLogin) {
        this.ldapLogin = ldapLogin;
    }
    
    /**
     * Submit the given token and redirect to the vote.xhtml with the poll linked to that token.
     * @return the url to navigate to after submitting a token
     */
    public String submitToken() {
        return "vote.xhtml?faces-redirect=true&amp;token=" +getToken();
    }
}
