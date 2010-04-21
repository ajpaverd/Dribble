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
    //Initialise the queueing service variables
     String                  queueName = null;
     InitialContext          jndiContext = null;
     //InitialContext         jndiContext1 = null;
     TopicConnectionFactory  topicConnectionFactory = null;
     TopicConnection         topicConnection = null;
     TopicSession            topicSession = null;
     Topic                   topic = null;
     TopicSubscriber         topicSubscriber = null;
     TextMessage             message = null;
     ObjectMessage           object = null;
     final int               NUM_MSGS=4;

     
    /** Creates a new instance of GetDribsResource */
    public GetDribsResource() {
        logger.info("Inside the constructor");

        try {
            jndiContext =  new InitialContext();
            logger.info("JNDI Context Initialised");
            //Connection factory and topic

            logger.info("Looking up topic");
            topic = (Topic) jndiContext.lookup("jms/getDribTopic");
            logger.info("lookup topic connection factory");
            topicConnectionFactory = (TopicConnectionFactory)jndiContext.lookup("jms/getDribTopicFactoryPool");
            logger.info("Lookup context complete");
        } catch (NamingException e) {
            logger.info("JNDI API lookup failed: " +
                e.toString());
        }
        //Create a topic connection, a session and a sender object to send the message
        try {
            logger.info("create topic connection");
        topicConnection = topicConnectionFactory.createTopicConnection();
        logger.info("created, now create topic session");
        topicSession =  topicConnection.createTopicSession(true,Session.AUTO_ACKNOWLEDGE);
        logger.info("created, now create topic sender");
        topicSubscriber = topicSession.createSubscriber(topic);

        }
        catch(JMSException e){
            System.out.println("Exception occurred: " +
                e.toString());
        }

    }

    /**
     * Retrieves representation of an instance of dribble.communications.GetDribsResource
     * @return an instance of java.util.ArrayList
     */
    @GET
    @Produces("application/xml")
    public List<Drib> getXml() {


        logger.info("GET Request");
       
        try{

            topicSubscriber.receive();
            logger.info("Object Received");
        }catch(JMSException jmse2){

        }
        List<Drib> resp = new ArrayList<Drib>();

        /*Drib a = new Drib();
        a.setText("Topic A");
        a.setPopularity(10000);


        Drib b = new Drib();
        b.setText("Topic B");
        b.setPopularity(10);


        Drib c = new Drib();
        c.setText("Topic C");
        c.setPopularity(100);

        resp.add(a);
        resp.add(b);
        resp.add(c);*/

        return resp;


//        throw new UnsupportedOperationException("Bad message");
    }

    /**
     * PUT method for updating or creating an instance of GetDribsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(List content) {
        throw new UnsupportedOperationException();
    }
}
