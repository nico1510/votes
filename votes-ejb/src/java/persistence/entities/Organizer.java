package persistence.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import logic.to.OrganizerTO;

@Entity
public class Organizer extends AbstractEntity<Organizer, OrganizerTO> {

    /**
     *
     */
    private static final long serialVersionUID = -7851649605912104684L;
    private String username;
    private String realname;
    private String email;
    private List<Poll> polls;
    private List<Poll> createdPolls;
    private List<MailingList> mailinglists;

    public Organizer() {
        polls = new ArrayList<>();
        createdPolls = new ArrayList<>();
        mailinglists = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToMany(mappedBy = "organizers")
    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }
    
    /**
     * @return the createdPolls
     */
    @OneToMany(mappedBy = "creator")
    public List<Poll> getCreatedPolls() {
        return createdPolls;
    }

    /**
     * @param createdPolls the createdPolls to set
     */
    public void setCreatedPolls(List<Poll> createdPolls) {
        this.createdPolls = createdPolls;
    }

    /**
     * @return the mailinglists
     */
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval= true)
    public List<MailingList> getMailinglists() {
        return mailinglists;
    }

    /**
     * @param mailinglists the mailinglists to set
     */
    public void setMailinglists(List<MailingList> mailinglists) {
        this.mailinglists = mailinglists;
    }

    @Override
    public OrganizerTO createTO() {
        OrganizerTO organizerTO = new OrganizerTO();
        organizerTO.setId(getId());
        organizerTO.setEmail(getEmail());
        organizerTO.setRealname(getRealname());
        organizerTO.setUsername(getUsername());
        
        return organizerTO;
    }

}
