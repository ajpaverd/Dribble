package dribble.processing;

import dribble.common.*;
import dribble.dataset.*;

import java.util.ArrayList;
import java.util.Date;
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

/**
 *
 * @author Dribble
 */
@MessageDriven(mappedName = "jms/getDribsQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class GetDribsBean implements MessageListener {

    static final Logger logger = Logger.getLogger("GetDribsBean");
    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Dataset dataset;
    Date t = new Date();

    public GetDribsBean() {
        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");

            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribsQueueFactoryPool");
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
            logger.severe("JMS exception occurred: " + jmse.getMessage());
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
                int subjectID = message.getIntProperty("subjectID");

                ArrayList<Drib> dribList = getDribs(latitude, longitude, results, subjectID);

                Message reply = queueSession.createObjectMessage(dribList);

                logger.info("sending reply");
                sender.send(reply);
                logger.info("sent reply");

                sender.close();
            }

        } catch (JMSException jmse) {
            logger.info("JMS exception: " + jmse.getMessage());
        }

    }

    public ArrayList<Drib> getDribs(double latitude, double longitude, int results, int subjectID) {

        logger.info("Latitude: " + latitude);
        logger.info("Longitude: " + longitude);
        logger.info("Results: " + results);
        logger.info("SubjectID: " + subjectID);

        ArrayList<Drib> resp = new ArrayList<Drib>();

        Drib a = new Drib();

        a.setText("There is a fire in the hole");
        a.setPopularity(10000);
        a.setLatitude(0.4343434);
        a.setLongitude(0.436746);
        a.setLikeCount(5);
        a.setTime(t.getTime());
        DribSubject dribsubject = new DribSubject();
        a.setSubject(dribsubject);
        a.getSubject().setName("Fire");
        a.getSubject().setLatitude(0.4343443432);
        a.getSubject().setLongitude(0.3232);
        //a.getSubject().setNumPosts(0);
        a.getSubject().setSubjectID(25);
        //a.getSubject().s

        Drib b = new Drib();
        b.setText("Drib B");
        b.setPopularity(10);

        Drib c = new Drib();
        c.setText("Drib C");
        c.setPopularity(100);

        resp.add(a);
        resp.add(b);
        resp.add(c);

        logger.info("Reply created");

        return resp;

    }

    @Override
    protected void finalize() throws Throwable {

        queueSession.close();
        queueConnection.close();

        super.finalize();
    }
}
