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
public class TokenTO extends AbstractEntityTO {

    /**
     *
     */
    private static final long serialVersionUID = 8064022421122361779L;
    private String tokenValue;
    private boolean valid;


    /**
     * @return the tokenValue
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * @param tokenValue the tokenValue to set
     */
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
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

}
