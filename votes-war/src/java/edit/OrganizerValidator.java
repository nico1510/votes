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
@FacesValidator("votes.OrganizerValidator")
public class OrganizerValidator implements Validator {

    @EJB
    private VotesLogic logic;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String name = value.toString();
        if (name.isEmpty()) {
            return;
        }
        
        if (name.contains("@")) {
            String[] split = name.split("@");
            name = split[0];
        }
                
        if (!logic.existsOrganizer(name)) {
            String message = context.getApplication().getResourceBundle(context, "msgs").getString("organizer.notFound");
            FacesMessage msg = new FacesMessage(message, message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

}
