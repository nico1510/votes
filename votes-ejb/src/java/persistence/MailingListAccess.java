package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.MailingListTO;
import persistence.entities.MailingList;

@Stateless
@LocalBean
public class MailingListAccess extends AbstractAccess<MailingList, MailingListTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public MailingListAccess() {
        super(MailingList.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
}
