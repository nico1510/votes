package persistence;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import logic.to.PollTO;
import persistence.entities.Poll;
import persistence.entities.PollState;

@Stateless
@LocalBean
public class PollAccess extends AbstractAccess<Poll, PollTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;

    /**
     * Default constructor.
     */
    public PollAccess() {
        super(Poll.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Poll getPollByToken(String token) {
        try {
            return em.createQuery("SELECT p"
                    + " FROM Poll p, IN(p.tokens) t"
                    + " WHERE t.tokenValue = :token",
                    Poll.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Poll> getPollAllPollsForOrganizer(long organizerID) {
        try {
            return em.createQuery("SELECT p"
                    + " FROM Poll p join p.organizers o"
                    + " WHERE o.id = :organizerId",
                    Poll.class)
                    .setParameter("organizerId", organizerID)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long getRemainingVotesCount(long pollID) {
        try {
            return em.createQuery(
                    "SELECT COUNT(t) FROM Token t"
                    + " WHERE t.poll.id = :pollId"
                    + " AND t.valid = TRUE",
                    Long.class)
                    .setParameter("pollId", pollID)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public void updateState(Long pollID, PollState newState) {
        em.createQuery("UPDATE Poll p SET p.pollState = :state"
                + " WHERE p.id = :pollId")
                .setParameter("pollId", pollID)
                .setParameter("state", newState)
                .executeUpdate();
    }

    public Poll getPollByMasterToken(String masterToken) {
        return super.findBy("masterToken", masterToken);
    }

}
