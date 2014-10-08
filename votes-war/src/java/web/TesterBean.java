package web;


import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.PollTO;
import login.UserBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nico
 */
@ViewScoped
@Named
public class TesterBean implements Serializable {
    
    @EJB
    VotesLogic votesLogic;
    @Inject
    UserBean userBean;
    private long pollID;
    private List<PollTO> myPolls;
    
    
    public String generatePreparedTestPoll() {
        votesLogic.generatePreparedTestPoll();
        
        return "organizer/overview.xhtml?faces-redirect=true";
    }
    
    public String generateProhibitedTestPoll() {
        votesLogic.generateProhibitedTestPoll();
        
        return "organizer/overview.xhtml?faces-redirect=true";
    }
    
    public String generatePublishedTestPoll() {
        votesLogic.generatePublishedTestPoll();
        
        return "view.xhtml?faces-redirect=true&amp;pollid=abc";
    }
    
    public String generateFinishedTestPoll() {
        votesLogic.generateFinishedTestPoll();
        
        return "organizer/overview.xhtml?faces-redirect=true";
    }
    
    public String generateStartedTestPoll() {
        votesLogic.generateStartedTestPoll();
        
        return "organizer/overview.xhtml?faces-redirect=true";
    }
        
    /**
     * @return the pollID
     */
    public long getPollID() {
        return pollID;
    }

    /**
     * @param pollID the pollID to set
     */
    public void setPollID(long pollID) {
        this.pollID = pollID;
    }

    /**
     * @return the myPolls
     */
    public List<PollTO> getMyPolls() {
        return votesLogic.getAllPollsForOrganizer(userBean.getOrganizer().getId());
    }

    /**
     * @param myPolls the myPolls to set
     */
    public void setMyPolls(List<PollTO> myPolls) {
        this.myPolls = myPolls;
    }
    
}
