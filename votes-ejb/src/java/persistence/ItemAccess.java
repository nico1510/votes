package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.ItemTO;
import persistence.entities.Item;

@Stateless
@LocalBean
public class ItemAccess extends AbstractAccess<Item, ItemTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public ItemAccess() {
        super(Item.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public void increaseAbstentions(long itemID){
        em.createQuery("UPDATE Item it SET it.abstentions = it.abstentions + 1"
					+ " WHERE it.id = :itemId",
					Long.class)
					.setParameter("itemId", itemID).executeUpdate();
    }
    
}
