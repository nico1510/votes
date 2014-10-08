package persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import logic.to.ParticipantTO;
import persistence.entities.Participant;

@Stateless
@LocalBean
public class ParticipantAccess extends AbstractAccess<Participant, ParticipantTO> {

    @PersistenceContext(name = "votes-ejbPU")
    EntityManager em;
    
    /**
     * Default constructor. 
     */
    public ParticipantAccess() {
        super(Participant.class);
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
