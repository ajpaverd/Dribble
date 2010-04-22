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
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

/**
 * REST Web Service
 *
 * @author Dribble
 */
@Path("PutDrib")
public class PutDribResource {

    @Context
    private UriInfo context;
    static final Logger logger = Logger.getLogger("PutDribResource");
    //Initialise the queueing service variables
    String queueName = null;
    InitialContext jndiContext = null;
    QueueConnectionFactory queueConnectionFactory = null;
    QueueConnection queueConnection = null;
    QueueSession queueSession = null;
    Queue queue = null;
    QueueSender queueSender = null;
    TextMessage message = null;
    ObjectMessage object = null;
    final int NUM_MSGS = 4;

    /** Creates a new instance of PutDribResource */
    public PutDribResource() {

        logger.info("PutDribResource Constructor");

        try {
            jndiContext = new InitialContext();
            logger.info("JNDI Context Initialised");
            //Connection factory and queue

            logger.info("Looking up queue");
            queue = (Queue) jndiContext.lookup("jms/putDribQueue");
            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/putDribQueueFactoryPool");
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
            logger.info("created, now create queue sender");
            queueSender = queueSession.createSender(queue);

        } catch (JMSException e) {
            System.out.println("Exception occurred: "
                    + e.toString());
        }
    }

    /**
     * Retrieves representation of an instance of dribble.communications.PutDribResource
     * @return an instance of dribble.common.Drib
     */
    @GET
    @Produces("application/xml")
    public Drib getXml() {
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PutDribResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(Drib content) {

        logger.info("Put Request");
        try {
            object = queueSession.createObjectMessage(content);
            queueSender.send(object);
            logger.info("Object Sent");
        } catch (JMSException jmse2) {
            logger.info("Error...");
        }
        
    }
}