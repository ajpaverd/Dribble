/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.processing;


import dribble.common.*;
import java.util.ArrayList;
import java.util.List;


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
 * @author Dribble
 */
@MessageDriven(mappedName = "jms/getDribsQueue", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
public class GetDribsBean implements MessageListener {

    static final Logger logger = Logger.getLogger("GetDribsBean");

    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;

    public GetDribsBean() {
        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");

            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory)jndiContext.lookup("jms/getDribsQueueFactoryPool");
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

        ArrayList<Drib> resp = new ArrayList<Drib>();

        Drib a = new Drib();
        a.setText("There is a fire in the hole");
        a.setPopularity(10000);
        a.setLatitude(0.4343434);
        a.setLongitude(0.436746);
        a.setLikeCount(5);
        //a.setTime(System.currentTimeMillis());
        DribSubject dribsubject = new DribSubject();
        a.setSubject(dribsubject);
        a.getSubject().setName("Fire");
        a.getSubject().setLatitude(0.4343443432);
        a.getSubject().setLongitude(0.3232);
        //a.getSubject().setNumPosts(0);
        a.getSubject().setSubjectID(25);
        //a.getSubject().s

        Drib b = new Drib();
        b.setText("Topic B");
        b.setPopularity(10);



        Drib c = new Drib();
        c.setText("Topic C");
        c.setPopularity(100);

        resp.add(a);
        resp.add(b);
        resp.add(c);

        logger.info("Reply created");

        //double lat = message.getDoubleProperty("latitude");
        //double lon = message.getDoubleProperty("longitude");
        //int num = message.getIntProperty("num_messages");
        //logger.info("Lat: " + lat);
        //logger.info("Lon: " + lon);
        //logger.info("Num: " + num);


        Message reply = queueSession.createObjectMessage(resp);

        logger.info("sending reply");
        sender.send(reply);
        logger.info("sent reply");



        } catch(JMSException jmse) {
            logger.info("problem");
        }





    }
    
}
