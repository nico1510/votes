/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author nico
 */
public class MailingListTO extends AbstractEntityTO {

    private String name;
    private String mailAddresses;

    
    public List<String> getMailAdressList() {
        List<String> list = new ArrayList<>();
        if (getMailAddresses() != null) {
            String[] splits = getMailAddresses().split(";");
            for(String split : splits) {
                if (isEmail(split)) {
                    list.add(split);
                }
            }
        }
        return list;
    }
    
    public void setMailAddressList(List<String> addresses) {
        setMailAddresses("");
        for (String address : addresses) {
            if (isEmail(address)) {
                setMailAddresses(getMailAddresses().concat(address).concat(";"));
            }
        }
    }

    /**
     * Checks if an string is an email.
     *
     * @param email the string to check if it is an email
     * @return true if string is an email, false otherwise
     */
    private boolean isEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
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
     * @return the mailAddresses
     */
    public String getMailAddresses() {
        return mailAddresses;
    }

    /**
     * @param mailAddresses the mailAddresses to set
     */
    public void setMailAddresses(String mailAddresses) {
        this.mailAddresses = mailAddresses;
    }
    
}
