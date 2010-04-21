/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.communications;

import dribble.common.DribTopic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.logging.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author andrew
 */

@Path("GetDribTopics")
public class GetDribTopicsResource {
    @Context
    private UriInfo context;

    private Logger logger = Logger.getLogger("GetDribTopics");

    /** Creates a new instance of GetDribTopicsResource */
    public GetDribTopicsResource() {
    }

    /**
     * Retrieves representation of an instance of dribble.communications.GetDribTopicsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public List<DribTopic> getXml() {
        //TODO return proper representation object

        logger.info("GET requested from GetDribTopics - Yay!");


		   //TODO return proper representation object
        //Sends a list of topics to the user
        List<DribTopic> dribbletop= new ArrayList<DribTopic>();

        DribTopic t = new DribTopic();
        t.setName("Fire");
        t.setTime(new Date(System.currentTimeMillis()));
        DribTopic a = new DribTopic();
        a.setName("Water");
        a.setTime(new Date(System.currentTimeMillis()));
        DribTopic b = new DribTopic();
        b.setName("Earth");
        b.setTime(new Date(System.currentTimeMillis()));

        dribbletop.add(t);
        dribbletop.add(a);
        dribbletop.add(b);
        return dribbletop;

    }

    /**
     * PUT method for updating or creating an instance of GetDribTopicsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
