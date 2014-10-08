/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

/**
 *
 * @author nico
 */
public class ParticipantTO extends AbstractEntityTO {

    /**
     *
     */
    private static final long serialVersionUID = -108167005799719514L;
    private String email;
    private boolean hasVoted;
    private TokenTO token;

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the hasVoted
     */
    public boolean isHasVoted() {
        return hasVoted;
    }

    /**
     * @param hasVoted the hasVoted to set
     */
    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    /**
     * @return the token
     */
    public TokenTO getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(TokenTO token) {
        this.token = token;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParticipantTO)) {
            return false;
        } else {
            ParticipantTO otherParticipant = (ParticipantTO)o;
            return getEmail().equals(otherParticipant.getEmail());
        }
    }
}
