package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.ItemOptionTO;
import persistence.entities.ItemOption;

@Stateless
@LocalBean
public class ItemOptionAccess extends AbstractAccess<ItemOption, ItemOptionTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public ItemOptionAccess() {
        super(ItemOption.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public void voteForOption(long oID){
        em.createQuery("UPDATE ItemOption opt SET opt.votes = opt.votes + 1"
					+ " WHERE opt.id = :optionId")
					.setParameter("optionId", oID).executeUpdate();
    }
    
}
