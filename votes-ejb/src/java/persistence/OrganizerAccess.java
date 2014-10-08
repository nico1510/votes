package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.OrganizerTO;
import persistence.entities.Organizer;

@Stateless
@LocalBean
public class OrganizerAccess extends AbstractAccess<Organizer, OrganizerTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public OrganizerAccess() {
        super(Organizer.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public Organizer findByUsername(String username) {
        return super.findBy("username", username);
    }
    
}
