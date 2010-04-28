package dribble.communications;

import dribble.common.*;

import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.naming.InitialContext;
import javax.naming.NamingException;

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
@Path("PutDrib")
public class PutDribResource {

    //@Context
    //private UriInfo context;
    static final Logger logger = Logger.getLogger("PutDribResource");
    InitialContext jndiContext;
    QueueConnectionFactory queueConnectionFactory;
    QueueConnection queueConnection;
    QueueSession queueSession;
    Queue queue;
    QueueSender queueSender;

    /** Creates a new instance of PutDribResource */
    public PutDribResource() {

        logger.info("PutDribResource Constructor");

        try {

            jndiContext = new InitialContext();
            logger.info("Looking up queue");
            queue = (Queue) jndiContext.lookup("jms/putDribQueue");
            logger.info("Looking up queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/putDribQueueFactoryPool");
            logger.info("Create queue connection");
            queueConnection = queueConnectionFactory.createQueueConnection();
            logger.info("Create queue session");
            queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            logger.info("Create queue sender");
            queueSender = queueSession.createSender(queue);

            logger.info("PutDribResource instance constructed");

        } catch (NamingException ne) {
            logger.severe("JNDI API lookup failed: " + ne.toString());
        } catch (JMSException jmse) {
            logger.severe("Exception occurred: " + jmse.toString());
        }
    }

    /**
     * GET method unused in this webservice
     * @return UnsupportedOperationException
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
    public String putXml(Drib content) {

        logger.info("Put Request: " + content.getText());
        try {
            ObjectMessage msg = queueSession.createObjectMessage(content);
            queueSender.send(msg);
            logger.info("Drib sent to processing bean");
            return "success";
        } catch (JMSException jmse) {
            logger.severe("JMS Exception: " + jmse.getMessage());
            return "failure";
        }

    }

    @Override
    protected void finalize() throws Throwable {

        queueSession.close();
        queueConnection.close();

        super.finalize();
    }
}
