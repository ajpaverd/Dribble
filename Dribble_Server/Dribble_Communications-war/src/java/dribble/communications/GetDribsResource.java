/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dribble.communications;

//import dribble.common.Drib;
import java.util.ArrayList;
import java.util.List;

//import dribble.common.DribTopic;
//import javax.naming.InitialContext;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import javax.naming.NamingException;
import java.util.logging.Logger;

import java.util.Calendar;

import dribble.common.*;
import javax.naming.*;
import javax.jms.*;
import javax.ws.rs.core.MultivaluedMap;

/**
 * REST Web Service
 *
 * @author andrew
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
            //Connection factory and queue

            logger.info("Looking up queue");
            queue = (Queue)jndiContext.lookup("jms/getDribsQueue");
            logger.info("lookup queue connection factory");
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/getDribsQueueFactoryPool");
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
     * Retrieves representation of an instance of dribble.communications.GetDribsResource
     * @return an instance of java.util.ArrayList
     */
    @GET
    @Produces("application/xml")
    public ArrayList<Drib> getXml(/*@Context UriInfo ui*/) {

        logger.info("Get Request");
        /*if (ui ==null){
            return null;
            }
        MultivaluedMap<String,String> queryParams = ui.getQueryParameters();*/

       /*String latitudeString = queryParams.getFirst("latitude");
        String longitudeString = queryParams.getFirst("longitude");
        String resultsString = queryParams.getFirst("results");
        String subjectIDString = queryParams.getFirst("subjectID");

        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);
        int results = Integer.parseInt(resultsString);
        int subjectID = Integer.parseInt(subjectIDString);*/

        double latitude = 0.0438743;
        double longitude = 0.212871;

        try {

            Message msg = queueSession.createMessage();
            //msg.setDoubleProperty("latitude", latitude);
            //msg.setDoubleProperty("longitude", longitude);
            //msg.setIntProperty("results", results);
           // msg.setIntProperty("subjectID", subjectID);

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

            Message response = receiver.receive(10000);

            logger.info("Message received");
            if(response == null) {
                logger.info("Timeout");
            }

            ObjectMessage oresponse = (ObjectMessage)response;

            logger.info("Object message created");

            ArrayList<Drib> dribList = (ArrayList<Drib>) oresponse.getObject();

            return dribList;


        } catch (JMSException jmse2) {
            logger.info("Error...");
            return null;
        }

    }



    /**
     * PUT method for updating or creating an instance of GetDribsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(ArrayList content) {
        throw new UnsupportedOperationException();
    }
}
