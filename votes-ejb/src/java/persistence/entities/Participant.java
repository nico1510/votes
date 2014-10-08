package persistence.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import logic.to.ParticipantTO;

/**
 * @author adaudrich
 */
@Entity
public class Participant extends AbstractEntity<Participant, ParticipantTO> {

    /**
     *
     */
    private static final long serialVersionUID = -2902499488767060342L;
    private String email;
    private boolean hasVoted;
    private Poll poll;
    private Token token;

    public Participant() {
        hasVoted = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    @ManyToOne
    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    @OneToOne
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public ParticipantTO createTO() {
        ParticipantTO participantTO = new ParticipantTO();
        participantTO.setId(getId());
        participantTO.setEmail(getEmail());
        participantTO.setHasVoted(getHasVoted());
        if (getToken() != null) {
            participantTO.setToken(getToken().createTO());
        } else {
            participantTO.setToken(null);
        }

        return participantTO;
    }

}
