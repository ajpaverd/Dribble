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
@Path("GetDribSubjects")
public class GetDribSubjectsResource {

    //@Context
    //private UriInfo context;
    static final Logger logger = Logger.getLogger("GetDribSubjectsResource");
    private InitialContext jndiContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Queue queue;
    private QueueSender queueSender;

    /** Creates a new instance of PutDribResource */
    public GetDribSubjectsResource() {

        logger.info("GetDribSubjectsResource Constructor");

        try {

            jndiContext = new InitialContext();
            logger.info("Looking up queue");
            queue = (Queue) jndiContext.lookup("jms/getDribSubjectsQueue");
            logger.info("looking up queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribSubjectsQueueFactoryPool");
            logger.info("Create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("Create queue session");
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("Create queue sender");
            queueSender = queueSession.createSender(queue);

            logger.info("GetDribSubjectsResource instance constructed");

        } catch (NamingException e) {
            logger.severe("JNDI API lookup failed: " + e.toString());
        } catch (JMSException e) {
            logger.severe("Exception occurred: " + e.toString());
        }
    }

    /**
     * Retrieves representation of an instance of dribble.communications.GetDribSubjectsResource
     * @return an instance of dribble.common.DribSubject
     */
    @GET
    @Produces("application/xml")
    public DribSubjectList getXml(@Context UriInfo ui) {

        logger.info("Get subjects request");

        if (ui == null) {
            logger.severe("Missing url parameters");
            return null;
        }

        try {

            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();

            String latitudeString = queryParams.getFirst("latitude");
            String longitudeString = queryParams.getFirst("longitude");
            String resultsString = queryParams.getFirst("results");

            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeString);
            int results = Integer.parseInt(resultsString);

            Message msg = queueSession.createMessage();
            msg.setDoubleProperty("latitude", latitude);
            msg.setDoubleProperty("longitude", longitude);
            msg.setIntProperty("results", results);

            logger.info("Request parameters received");

            //Create a temporary destination
            TemporaryQueue respDest = queueSession.createTemporaryQueue();

            //Set destination for the reply message
            msg.setJMSReplyTo(respDest);

            QueueReceiver receiver = queueSession.createReceiver(respDest);

            logger.info("Starting queue connection");

            queueConnection.start();

            logger.info("Sending request to processing bean");

            queueSender.send(msg);

            logger.info("Waiting for response");

            ObjectMessage response = (ObjectMessage) receiver.receive(10000);

            receiver.close();
            respDest.delete();

            if (response != null) {

                logger.info("Response received");


                ArrayList<DribSubject> subjectList = (ArrayList<DribSubject>) response.getObject();

                logger.info("Wrapping list of subjects");

                DribSubjectList wrapperList = new DribSubjectList();
                wrapperList.list = subjectList;

                logger.info("===== DribSubjects sent to client =====");

                return wrapperList;

            } else {

                logger.severe("Response not received - timeout");

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
     * PUT method unused for this webservice
     * @return UnsupportedOperationException
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(DribSubject content) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void finalize() throws Throwable {
        
        queueSender.close();
        queueSession.close();
        queueConnection.close();

        super.finalize();
    }
}
