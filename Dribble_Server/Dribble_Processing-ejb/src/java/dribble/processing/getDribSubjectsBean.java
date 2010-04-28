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
            logger.info("Constructing GetDribSubjectBean");

            jndiContext = new InitialContext();
            logger.info("Looking up queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribSubjectsQueueFactoryPool");
            logger.info("Create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("Create queue session");
            queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            logger.info("Create Dataset connection");
            dataset = new SQLCommunicator();

            logger.info("GetDribSubjectsBean instance created");

        } catch (NamingException ne) {
            logger.severe("JNDI API lookup failed: " + ne.getMessage());

        } catch (JMSException jmse) {
            logger.severe("JMS exception: " + jmse.getMessage());
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

                ArrayList<DribSubject> subjectList = getDribSubjects(latitude, longitude, results);

                Message reply = queueSession.createObjectMessage(subjectList);

                logger.info("Sending response");

                sender.send(reply);

                sender.close();
            }


        } catch (JMSException jmse) {
            logger.info("JMS exception: " + jmse.getMessage());
        }

    }

    public ArrayList<DribSubject> getDribSubjects(int latitude, int longitude, int results) {

        logger.info("Request parameter: latitude = " + latitude);
        logger.info("Request parameter: longitude = " + longitude);
        logger.info("Request parameter: results = " + results);

        logger.info("Retrieving DribSubjects from dataset");
        //ArrayList<DribSubject> subjectList = dataset.getDribSubjects(latitude, longitude, 25000);
        ArrayList<DribSubject> subjectList = testSubjectList();

        logger.info("Calculating popularity scores");

        subjectList = DribblePopularity.rankSubjects(subjectList, latitude, longitude);

        while(subjectList.size() > results) {
            subjectList.remove(results);
        }
        //TODO resize list

        return subjectList;
    }

    private ArrayList<DribSubject> testSubjectList() {

        ArrayList<DribSubject> testList = new ArrayList<DribSubject>();

        DribSubject a = new DribSubject();
        a.setName("DribSubject A");
        a.setNumViews(2);
        a.setNumPosts(2);
        a.setLatitude(108000);
        a.setLongitude(108000);
        a.setTime(System.currentTimeMillis()-300000);
        a.setSubjectID(25);


        DribSubject b = new DribSubject();
        b.setName("DribSubject B");
        b.setNumViews(2);
        b.setNumPosts(2);
        b.setLatitude(108000);
        b.setLongitude(108000);
        b.setTime(System.currentTimeMillis()-60000);
        b.setSubjectID(26);


        DribSubject c = new DribSubject();
        c.setName("DribSubject C");
        c.setNumViews(2);
        c.setNumPosts(2);
        c.setLatitude(108000);
        c.setLongitude(108000);
        c.setTime(System.currentTimeMillis()-300000);
        c.setSubjectID(27);

        testList.add(a);
        testList.add(b);
        testList.add(c);

        return testList;

    }

    @Override
    protected void finalize() throws Throwable {

        queueSession.close();
        queueConnection.close();

        super.finalize();
    }
}
