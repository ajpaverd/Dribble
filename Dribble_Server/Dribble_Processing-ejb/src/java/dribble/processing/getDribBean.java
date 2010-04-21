/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.processing;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.*;

/**
 *
 * @author Daniel
 */
@MessageDriven(mappedName = "jms/getDribTopic", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "getDribBean"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "getDribBean")
    })
public class getDribBean implements MessageListener {
    TopicPublisher topicPublisher = null;

    public getDribBean() {
    }

    public void onMessage(Message message) {
        try {
            //message.toString();
           
            topicPublisher.publish(message);
        } catch (JMSException ex) {
            Logger.getLogger(getDribBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
