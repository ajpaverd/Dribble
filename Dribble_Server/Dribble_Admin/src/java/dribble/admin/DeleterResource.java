/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dribble.admin;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author Daniel
 */
@Path("deleter")
public class DeleterResource {

    static final Logger logger = Logger.getLogger("Dribble_Admin_Main");
    @Context
    private UriInfo context;
    private static Dribble_Deleter deleter;

    /** Creates a new instance of DeleterResource */
    public DeleterResource() {
    }

    /**
     * Retrieves representation of an instance of dribble.admin.DeleterResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getDeleter() {
        //TODO return proper representation object

        logger.info("Starting Dribble_Deleter...");

        if (deleter == null) {
            deleter = new Dribble_Deleter();
        }

        if (deleter.isAlive() == false) {
            deleter.start();
        }

        logger.info("Dribble_Deleter started");
        return "Deleter started";

    }

    /**
     * PUT method for updating or creating an instance of DeleterResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
