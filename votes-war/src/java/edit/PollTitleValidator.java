/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edit;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import logic.VotesLogic;

/**
 *
 * @author artur
 */
@FacesValidator("votes.PollTitleValidator")
public class PollTitleValidator implements Validator {

    @EJB
    private VotesLogic logic;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String name = value.toString();
        Long id = (Long)component.getAttributes().get("pollId");
        
        if (name.isEmpty()) {
            return;
        }
                
        if (logic.existsPollTitle(name, id)) {
            String message = context.getApplication().getResourceBundle(context, "msgs").getString("poll.duplicateTitle");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

}
