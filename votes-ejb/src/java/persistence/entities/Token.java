package persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import logic.to.TokenTO;

@Entity
public class Token extends AbstractEntity<Token, TokenTO> {

    /**
     *
     */
    private static final long serialVersionUID = 225231063092624738L;
    private String tokenValue;
    private Participant participant;
    private Poll poll;
    private boolean valid;

    public Token() {

    }
    
    @Column(unique = true)
    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    @OneToOne(mappedBy = "token")
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @ManyToOne
    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
    
    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    @Override
    public TokenTO createTO() {
        TokenTO tokenTO = new TokenTO();
        tokenTO.setId(getId());
        tokenTO.setTokenValue(getTokenValue());
        tokenTO.setValid(isValid());

        return tokenTO;
    }

}
