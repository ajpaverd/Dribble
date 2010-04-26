package dribble.processing;

import dribble.common.*;
import dribble.dataset.*;
import java.util.ArrayList;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 *
 * @author Dribble
 */
@MessageDriven(mappedName = "jms/getDribSubjectsQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class getDribSubjectsBean implements MessageListener {

    static final Logger logger = Logger.getLogger("GetDribSubjectsBean");
    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Dataset dataset;

    public getDribSubjectsBean() {
        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");

            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribSubjectsQueueFactoryPool");
            logger.info("Lookup context complete");

            //Create a queue connection, a session and a sender object to send the message
            logger.info("create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("created, now create queue session");
            queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            dataset = new SQLCommunicator();

        } catch (NamingException ne) {
            logger.severe("JNDI API lookup failed: " + ne.getMessage());

        } catch (JMSException jmse) {
            logger.severe("JMS exception: " + jmse.getMessage());
        }



    }

    public void onMessage(Message message) {

        logger.info("OnMessage");

        try {

            Queue dest = (Queue) message.getJMSReplyTo();

            if (dest != null) {

                logger.info("Reply queue: " + dest.toString());

                //create a producer instance
                QueueSender sender = queueSession.createSender(dest);

                double latitude = message.getDoubleProperty("latitude");
                double longitude = message.getDoubleProperty("longitude");
                int results = message.getIntProperty("results");

                ArrayList<DribSubject> subjectList = getDribSubjects(latitude, longitude, results);

                Message reply = queueSession.createObjectMessage(subjectList);

                logger.info("sending reply");
                sender.send(reply);
                logger.info("sent reply");
            }


        } catch (JMSException jmse) {
            logger.info("JMS exception: " + jmse.getMessage());
        }

    }

    public ArrayList<DribSubject> getDribSubjects(double latitude, double longitude, int results) {

        logger.info("Latitude: " + latitude);
        logger.info("Longitude: " + longitude);
        logger.info("Results: " + results);

        ArrayList<DribSubject> resp = new ArrayList<DribSubject>();

        DribSubject a = new DribSubject();
        a.setName("DribSubject A");
        a.setPopularity(10000);


        DribSubject b = new DribSubject();
        b.setName("DribSubject B");
        b.setPopularity(10);


        DribSubject c = new DribSubject();
        c.setName("DribSubject C");
        c.setPopularity(100);

        resp.add(a);
        resp.add(b);
        resp.add(c);

        logger.info("Reply created");

        return resp;
    }
}
