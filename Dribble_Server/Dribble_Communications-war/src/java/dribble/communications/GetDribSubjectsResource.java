package dribble.communications;

import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import java.util.logging.Logger;


import dribble.common.*;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueRequestor;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.ws.rs.core.MultivaluedMap;

/**
 * REST Web Service
 *
 * @author Dribble
 */
@Path("GetDribSubjects")
public class GetDribSubjectsResource {

    @Context
    private UriInfo context;
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
            logger.info("JNDI Context Initialised");
            //Connection factory and queue

            logger.info("Looking up queue");
            queue = (Queue)jndiContext.lookup("jms/getDribSubjectsQueue");
            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribSubjectsQueueFactoryPool");
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
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("created, now create queue requestor");
            queueSender = queueSession.createSender(queue);
            //queueRequestor = new QueueRequestor(queueSession, queue);
            logger.info("queue requestor created");

        } catch (JMSException e) {
            System.out.println("Exception occurred: "
                    + e.toString());
        }
    }

    /**
     * Retrieves representation of an instance of dribble.communications.GetDribSubjectsResource
     * @return an instance of dribble.common.DribSubject
     */
    @GET
    @Produces("application/xml")
    public DribSubject getXml(@Context UriInfo ui) {
        logger.info("Get Request");

        MultivaluedMap<String,String> queryParams = ui.getQueryParameters();

        String latitudeString = queryParams.getFirst("latitude");
        String longitudeString = queryParams.getFirst("longitude");
        String resultsString = queryParams.getFirst("results");

        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);
        int results = Integer.parseInt(resultsString);

        try {

            Message msg = queueSession.createMessage();
            msg.setDoubleProperty("latitude", latitude);
            msg.setDoubleProperty("longitude", longitude);
            msg.setIntProperty("results", results);

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

            logger.info("Sent omsg");

            Message response = receiver.receive(10000);

            logger.info("Message received");
            if(response == null) {
                logger.info("Timeout");
            }

            ObjectMessage oresponse = (ObjectMessage)response;

            logger.info("Object message created");

            DribSubject ds = (DribSubject)oresponse.getObject();

            return ds;


        } catch (JMSException jmse2) {
            logger.info("Error...");
            return null;
        }
    }

    /**
     * PUT method for updating or creating an instance of GetDribSubjectsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(DribSubject content) {
        throw new UnsupportedOperationException();
    }
}
