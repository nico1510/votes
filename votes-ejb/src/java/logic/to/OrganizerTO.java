/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author nico
 */
public class OrganizerTO extends AbstractEntityTO {

    /**
     *
     */
    private static final long serialVersionUID = 1512901406434708637L;
    private String username;
    private String realname;
    private String email;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the realname
     */
    public String getRealname() {
        return realname;
    }

    /**
     * @param realname the realname to set
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrganizerTO)) {
            return false;
        } else {
            OrganizerTO otherOrganizer = (OrganizerTO) o;
            return getUsername().equals(otherOrganizer.getUsername());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.username);
        return hash;
    }
}
