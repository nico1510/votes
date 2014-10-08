/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import logic.to.MailingListTO;

/**
 *
 * @author nico
 */
@Entity
public class MailingList extends AbstractEntity<MailingList, MailingListTO> {
    
    private String name;
    private String mailAdresses;
    private Organizer organizer;
    
    public MailingList() {
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the mailAdresses
     */
    @Lob
    public String getMailAdresses() {
        return mailAdresses;
    }

    /**
     * @param mailAdresses the mailAdresses to set
     */
    public void setMailAdresses(String mailAdresses) {
        this.mailAdresses = mailAdresses;
    }

    /**
     * @return the organizer
     */
    @ManyToOne
    public Organizer getOrganizer() {
        return organizer;
    }

    /**
     * @param organizer the organizer to set
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }
    
    @Override
    public MailingListTO createTO() {
        MailingListTO mailingListTO = new MailingListTO();
        mailingListTO.setId(getId());
        mailingListTO.setMailAddresses(getMailAdresses());
        mailingListTO.setName(getName());
        
        return mailingListTO;
    }
}
