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

        logger.info("Constructing PutDribBean");

        dataset = new SQLCommunicator();

        logger.info("PutDribBean instance constructed");
    }

    public void onMessage(Message message) {

        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                Drib drib = (Drib) objMessage.getObject();
                putDrib(drib);
            } else {
                logger.severe("Invalid JMS message type");
            }

        } catch (JMSException jmse) {
            logger.severe("JMS excpeption: " + jmse.getMessage());
        }
    }

    //Once a drib has been received, use this method to start processing
    public void putDrib(Drib drib) {

        logger.info("PutDrib: " + drib.getText());

        //Set or update the times of the Drib and Subject
        drib.setTime(System.currentTimeMillis());
        drib.getSubject().setTime(System.currentTimeMillis());

        //Check if this is a new drib or an update
        if (drib.getMessageID() == 0) {
            addDrib(drib);
        } else {
            updateDrib(drib);
        }

    }

    //Add the new drib directly to the current dataset
    public void addDrib(Drib drib) {
        logger.info("New Drib: " + drib.getText());

        //Assign a new message ID
        drib.setMessageID(DribbleIdentifier.getUniqueID());

        if (drib.getSubject().getSubjectID() == 0) {
            //Assigin a new subject ID
            drib.getSubject().setSubjectID(DribbleIdentifier.getUniqueID());
        }

        //Add to dataset
        boolean result = dataset.addDrib(drib);

        if (result == true) {
            logger.info("===== Drib added to dataset =====");
        }

    }

    //Update the drib in the current dataset
    public void updateDrib(Drib drib) {
        logger.info("Update Drib: " + drib.getText());

        int dribID = drib.getMessageID();
        DribSubject subject = drib.getSubject();

        //Get the drib with corresponding ID from the dataset
        Drib existingDrib = dataset.getDrib(subject, dribID);

        if (existingDrib != null) {

            //Combine the existing like count with the -1, 0 or +1 update
            int likeCount = existingDrib.getLikeCount() + drib.getLikeCount();
            existingDrib.setLikeCount(likeCount);

            //Update the existing drib in the dataset
            boolean result = dataset.updateDrib(existingDrib);

            if (result == true) {
                logger.info("===== Drib updated in dataset =====");
            }

        } else {
            boolean result = dataset.addDrib(drib);

            if (result == true) {
                logger.info("===== Drib added to dataset =====");
            }

        }
    }
}
