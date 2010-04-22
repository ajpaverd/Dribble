/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.processing;

import dribble.common.*;
import java.util.logging.Level;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;
import javax.jms.ObjectMessage;
//import dribble.common.*;
/**
 *
 * @author Daniel
 */
@MessageDriven(mappedName = "jms/putDribQueue", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
public class putDribBean implements MessageListener {
     static final Logger logger = Logger.getLogger("PutDribBean");
    public putDribBean() {
    }

    public void onMessage(Message message) {

        try
        {
        if (message instanceof ObjectMessage)
        {
            ObjectMessage objMessage = (ObjectMessage) message;
            Drib drib = (Drib) objMessage.getObject();
            logger.info(drib.getText());
        }

        /*try {
            logger.info("We are in the actual message");
            ObjectMessage object = (ObjectMessage)message;

            logger.info("Object Deserialised");
            Drib drib = (Drib)object.getObject();
            
            logger.info("Casted Object");
            //String text = drib.getText();
            //logger.info("The name of the message is "+text);*/

        } catch (JMSException ex) {
            logger.info("Problem");
        }
    }
    
}
