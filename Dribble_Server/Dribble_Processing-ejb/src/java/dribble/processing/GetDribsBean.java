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

    public GetDribsBean() {
        try {

            logger.info("Constructing GetDribsBean");

            jndiContext = new InitialContext();
            logger.info("Looking up queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribsQueueFactoryPool");
            logger.info("Create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("Create queue session");
            queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            logger.info("Create Dataset connection");
            dataset = new SQLCommunicator();

            logger.info("GetDribsBean instance created");

        } catch (NamingException ne) {
            logger.severe("JNDI API lookup failed: " + ne.getMessage());

        } catch (JMSException jmse) {
            logger.severe("JMS exception occurred: " + jmse.getMessage());
        }

    }

    public void onMessage(Message message) {

        logger.info("Processing request received");

        try {

            Queue dest = (Queue) message.getJMSReplyTo();

            if (dest != null) {

                logger.info("Reply: " + dest.getQueueName());

                QueueSender sender = queueSession.createSender(dest);

                int latitude = message.getIntProperty("latitude");
                int longitude = message.getIntProperty("longitude");
                int results = message.getIntProperty("results");
                int subjectID = message.getIntProperty("subjectID");

                ArrayList<Drib> dribList = getDribs(latitude, longitude, results, subjectID);

                Message reply = queueSession.createObjectMessage(dribList);

                logger.info("Sending response");

                sender.send(reply);

                sender.close();
            }

        } catch (JMSException jmse) {
            logger.info("JMS exception: " + jmse.getMessage());
        }

    }

    public ArrayList<Drib> getDribs(int latitude, int longitude, int results, int subjectID) {

        logger.info("Request parameter: latitude = " + latitude);
        logger.info("Request parameter: longitude = " + longitude);
        logger.info("Request parameter: results = " + results);
        logger.info("Request parameter: subjectID = " + subjectID);

        logger.info("Retrieving DribSubjects from dataset");
        ArrayList<Drib> dribList = dataset.getDribs(subjectID, latitude, longitude, 25000);
        //ArrayList<Drib> dribList = testDribList();

        logger.info("Calculating popularity scores");

        dribList = DribblePopularity.rankDribs(dribList, latitude, longitude);

        while(dribList.size() > results) {
            dribList.remove(results);
        }

        return dribList;

    }

    public ArrayList<Drib> testDribList() {

        ArrayList<Drib> testList = new ArrayList<Drib>();

        Drib a = new Drib();
        a.setText("All the fires are out");
        a.setLatitude(108000);
        a.setLongitude(108000);
        a.setLikeCount(5);
        a.setTime(System.currentTimeMillis() - 300000);
        a.setMessageID(30);
        a.setSubject(new DribSubject());
        a.getSubject().setName("No Fire");
        a.getSubject().setLatitude(108000);
        a.getSubject().setLongitude(108000);
        a.getSubject().setNumPosts(3);
        a.getSubject().setSubjectID(25);
        a.getSubject().setPopularity(100);
        a.getSubject().setTime(System.currentTimeMillis()-300000);

        Drib b = new Drib();
        b.setText("Its cold now");
        b.setLatitude(108000);
        b.setLongitude(108000);
        b.setLikeCount(5);
        b.setTime(System.currentTimeMillis());
        b.setMessageID(40);
        b.setSubject(new DribSubject());
        b.getSubject().setName("No Fire");
        b.getSubject().setLatitude(108000);
        b.getSubject().setLongitude(108000);
        b.getSubject().setNumPosts(3);
        b.getSubject().setSubjectID(25);
        b.getSubject().setPopularity(100);
        b.getSubject().setTime(System.currentTimeMillis());

        Drib c = new Drib();
        c.setText("Hi Chad");
        c.setLatitude(108000);
        c.setLongitude(108000);
        c.setLikeCount(5);
        c.setTime(System.currentTimeMillis() - 150000);
        c.setMessageID(50);
        c.setSubject(new DribSubject());
        c.getSubject().setName("No Fire");
        c.getSubject().setLatitude(108000);
        c.getSubject().setLongitude(108000);
        c.getSubject().setNumPosts(3);
        c.getSubject().setSubjectID(25);
        c.getSubject().setPopularity(100);
        c.getSubject().setTime(System.currentTimeMillis()-150000);

        Drib d = new Drib();
        d.setText("Hi Ash");
        d.setLatitude(108000);
        d.setLongitude(108000);
        d.setLikeCount(5);
        d.setTime(System.currentTimeMillis() - 150000);
        d.setMessageID(51);
        d.setSubject(new DribSubject());
        d.getSubject().setName("No Fire");
        d.getSubject().setLatitude(108000);
        d.getSubject().setLongitude(108000);
        d.getSubject().setNumPosts(3);
        d.getSubject().setSubjectID(45);
        d.getSubject().setPopularity(100);
        d.getSubject().setTime(System.currentTimeMillis()-150000);

        Drib e = new Drib();
        e.setText("Hi Dribble");
        e.setLatitude(108000);
        e.setLongitude(108000);
        e.setLikeCount(5);
        e.setTime(System.currentTimeMillis() - 150000);
        e.setMessageID(52);
        e.setSubject(new DribSubject());
        e.getSubject().setName("No Fire");
        e.getSubject().setLatitude(108000);
        e.getSubject().setLongitude(108000);
        e.getSubject().setNumPosts(3);
        e.getSubject().setSubjectID(46);
        e.getSubject().setPopularity(100);
        e.getSubject().setTime(System.currentTimeMillis()-150000);

        testList.add(a);
        testList.add(b);
        testList.add(c);
        testList.add(d);
        testList.add(e);

        return testList;

    }

    @Override
    protected void finalize() throws Throwable {

        queueSession.close();
        queueConnection.close();

        super.finalize();
    }
}
