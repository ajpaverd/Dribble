/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.communications;

import java.util.logging.*;

import dribble.common.DribTopic;
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

@Path("PutDribTopic")
public class PutDribTopicResource {
    @Context
    private UriInfo context;

    private Logger logger = Logger.getLogger("PutDribTopic");

    /** Creates a new instance of PutDribTopicResource */
    public PutDribTopicResource() {
    }

    /**
     * Retrieves representation of an instance of dribble.communications.PutDribTopicResource
     * @return an instance of dribble.common.DribTopic
     */
    @GET
    @Produces("application/xml")
    public DribTopic getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PutDribTopicResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(DribTopic content) {

        logger.info("Get Detected");
        logger.info(content.getName());

    }
}
