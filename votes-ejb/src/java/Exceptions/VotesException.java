/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author nico
 */
@ApplicationException
public class VotesException extends Exception {

    public VotesException() {
    }

    public VotesException(String msg) {
        super(msg);
    }
}
