package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.TokenTO;
import java.util.List;
import persistence.entities.Token;

@Stateless
@LocalBean
public class TokenAccess extends AbstractAccess<Token, TokenTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public TokenAccess() {
        super(Token.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public Token findByTokenValue(String tokenValue) {
        return super.findBy("tokenValue", tokenValue);
    }
    
    public List<String> getAllUsedTokens() {
        return em.createQuery(
                "SELECT t.tokenValue FROM Token t",
                String.class)
                .getResultList();
    }
    
    /**
     * the poll has ended therefore all tokens are invalidated
     * @param pollID
     */
    public void invalidateAllTokensForPoll(long pollID) {
        em.createQuery("UPDATE Token t SET t.valid = FALSE"
					+ " WHERE t.poll.id = :pollId")
					.setParameter("pollId", pollID).executeUpdate();
    }


}
