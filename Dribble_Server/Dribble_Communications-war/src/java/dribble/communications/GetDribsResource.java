package dribble.communications;

import dribble.common.*;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Dribble
 */
@Path("GetDribs")
public class GetDribsResource {

    @Context
    private UriInfo context;
    static final Logger logger = Logger.getLogger("GetDribsResource");
    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Queue queue;
    private QueueSender queueSender;

    /** Creates a new instance of GetDribsResource */
    public GetDribsResource() {

        logger.info("GetDribsResource Constructor");

        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");

            logger.info("Looking up queue");
            queue = (Queue) jndiContext.lookup("jms/getDribsQueue");
            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribsQueueFactoryPool");
            logger.info("Lookup context complete");

            //Create a queue connection, a session and a sender object to send the message
            logger.info("create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("created, now create queue session");
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("created, now create queue requestor");
            queueSender = queueSession.createSender(queue);
            logger.info("queue sender created");
        } catch (NamingException e) {
            logger.severe("JNDI API lookup failed: " + e.toString());
        } catch (JMSException e) {
            logger.severe("Exception occurred: " + e.toString());
        }
    }

    /**
     * Retrieves representation of an instance of dribble.communications.GetDribsResource
     * @return an instance of java.util.ArrayList
     */
    @GET
    @Produces("application/xml")
    public DribList getXml(@Context UriInfo ui) {

        logger.info("Get Request");
        if (ui == null) {
            logger.severe("URI Info was null");
            return null;
        }

        try {

            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

            String latitudeString = queryParams.getFirst("latitude");
            String longitudeString = queryParams.getFirst("longitude");
            String resultsString = queryParams.getFirst("results");
            String subjectIDString = queryParams.getFirst("subjectID");

            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeString);
            int results = Integer.parseInt(resultsString);
            int subjectID = Integer.parseInt(subjectIDString);

            Message msg = queueSession.createMessage();
            msg.setDoubleProperty("latitude", latitude);
            msg.setDoubleProperty("longitude", longitude);
            msg.setIntProperty("results", results);
            msg.setIntProperty("subjectID", subjectID);

            logger.info("Message created");

            //create a temporary destination
            TemporaryQueue respDest = queueSession.createTemporaryQueue();

            //set destination for the ping_reply message
            msg.setJMSReplyTo(respDest);

            QueueReceiver receiver = queueSession.createReceiver(respDest);

            logger.info("created receiver");

            queueConnection.start();

            logger.info("connection started");

            queueSender.send(msg);

            logger.info("Sent msg");

            ObjectMessage response = (ObjectMessage) receiver.receive(10000);

            if (response != null) {

                logger.info("Message received");

                ArrayList<Drib> dribList = (ArrayList<Drib>) response.getObject();

                DribList wrapperList = new DribList();

                wrapperList.list = dribList;

                logger.info("Dribs Sent");

                return wrapperList;

            } else {

                logger.severe("Message not received - timeout");

                return null;
                
            }


        } catch (JMSException jmse) {
            logger.severe("JMS exception: " + jmse.getMessage());
            return null;
        } catch (NumberFormatException nfe) {
            logger.severe("Number format exception: " + nfe.getMessage());
            return null;
        } catch (NullPointerException npe) {
            logger.severe("Null pointer exception: " + npe.getMessage());
            return null;
        }

    }

    /**
     * PUT method unused in this webservice
     * @return UnsupportedOperationException
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(ArrayList content) {
        throw new UnsupportedOperationException();
    }
}
