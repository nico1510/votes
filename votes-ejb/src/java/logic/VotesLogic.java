package logic;

import Exceptions.VotesException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import logic.to.ItemTO;
import logic.to.MailingListTO;
import logic.to.OrganizerTO;
import logic.to.PollTO;

@Remote
public interface VotesLogic {
    
    public List<PollTO> getAllPollsForOrganizer(long organizerID);
    public List<PollTO> getAllPolls();
    public List<MailingListTO> getMailingListsForOrganizer(long organizerID);
    public PollTO getPoll(long id);
    public PollTO getPollByMasterToken(String masterToken) throws VotesException;
    public PollTO getPollByToken(String token) throws VotesException;
    public void savePoll(PollTO pollTO) throws VotesException;
    public void submitVotes(List<ItemTO> items, String token) throws VotesException;
    public OrganizerTO getOrganizerByUsername(String username) throws VotesException;
    public boolean existsOrganizer(String username);
    public void startPoll(long pollID) throws VotesException;
    public void extendVotingPeriod(long pollID, Date newEndDate) throws VotesException;
    public void deletePoll(long pollID, boolean asadmin) throws VotesException;
    public void updateMailingListsForOrganizer(long organizerId, List<MailingListTO> updatedLists);
    public void publishPoll(long pollID) throws VotesException;
    public void generateFinishedTestPoll();
    public void generatePublishedTestPoll();
    public void generatePreparedTestPoll();
    public void generateProhibitedTestPoll();
    public void generateStartedTestPoll();
    public boolean existsPollTitle(String title, Long pollId);

}
