/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.processing;

import dribble.common.*;


import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.InvalidDestinationException;

import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueRequestor;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author andrew
 */
@MessageDriven(mappedName = "jms/getDribSubjectsQueue", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
public class getDribSubjectsBean implements MessageListener {

    static final Logger logger = Logger.getLogger("GetDribSubjectsBean");

    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    
    public getDribSubjectsBean() {
        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");

            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory)jndiContext.lookup("jms/getDribSubjectsQueueFactoryPool");
            logger.info("Lookup context complete");
        } catch (NamingException e) {
            logger.info("JNDI API lookup failed: "
                    + e.toString());
        }
        //Create a queue connection, a session and a sender object to send the message
        try {
            logger.info("create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("created, now create queue session");
            queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);


        } catch (JMSException e) {
            System.out.println("Exception occurred: "
                    + e.toString());
        }
    }

    public void onMessage(Message message) {

        logger.info("OnMessage");

        try {

        //get the destination instance where the ping reply is to be sent.
        Queue dest = (Queue)message.getJMSReplyTo();

        if(dest == null) {
            logger.info("Dest is null");
        }

        logger.info("Got destination queue: " + dest.toString());

        //create a producer instance
        QueueSender sender = queueSession.createSender(dest);

        DribSubject ds = new DribSubject();
        ds.setName("ItWorks");

        logger.info("Reply created");

        ObjectMessage omsg = (ObjectMessage)message;
        String msg = (String)omsg.getObject();
        logger.info("Bean: "+msg);

        logger.info("creating reply");


        Message reply = queueSession.createObjectMessage(ds);

        logger.info("sending reply");
        sender.send(reply);
        logger.info("sent reply");



        } catch(JMSException jmse) {
            logger.info("problem");
        }

    }
    
}
