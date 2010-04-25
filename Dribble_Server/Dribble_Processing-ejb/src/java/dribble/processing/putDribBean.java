
package dribble.processing;


import dribble.common.*;
import dribble.dataset.*;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Dribble
 */

@MessageDriven(mappedName = "jms/putDribQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class putDribBean implements MessageListener {

    static final Logger logger = Logger.getLogger("PutDribBean");
    private Dataset dataset;

    public putDribBean() {

        dataset = new SQLCommunicator();

    }

    public void onMessage(Message message) {

        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                Drib drib = (Drib) objMessage.getObject();
                putDrib(drib);
            }

        } catch (JMSException jmse) {
            logger.severe("JMS excpeption: " + jmse.getMessage());
        }
    }


    public void putDrib(Drib drib) {
        
        logger.info("PutDrib: " + drib.getText());

        dataset.addDrib(drib);

    }
}
