/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package admin;

import Exceptions.VotesException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.PollTO;

/**
 *
 * @author nico
 */
@Named(value = "adminBean")
@ViewScoped
public class AdminBean {
    
    @EJB
    VotesLogic votesLogic;
    
    private List<PollTO> polls;
    
    @PostConstruct
    public void init() {
        setPolls(votesLogic.getAllPolls());
    }
    
    public String deletePoll(long pollId) {
        try {
            votesLogic.deletePoll(pollId, true);
        } catch (VotesException ex) {
            Logger.getLogger(AdminBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "delete.xhtml?faces-redirect=true";
    }

    /**
     * @return the polls
     */
    public List<PollTO> getPolls() {
        return polls;
    }

    /**
     * @param polls the polls to set
     */
    public void setPolls(List<PollTO> polls) {
        this.polls = polls;
    }
    
}
