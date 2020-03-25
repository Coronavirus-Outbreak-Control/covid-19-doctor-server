package org.coronaviruscheck.api.doctors.WebServer.Routes.API.App;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.APIExceptionHandler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/app")
public class HeartBeatAPI {

    public static Logger logger;

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {

        logger = LogManager.getLogger( this.getClass() );
        try {

            logger.info( "Pong..." );

            GenericResponse clientResponse = new GenericResponse();
            clientResponse.status = Response.Status.OK.getStatusCode();
            clientResponse.data = 1;
            clientResponse.message = "Pong...";
            return Response.ok( clientResponse ).build();

        } catch ( Exception bex ) {
            return APIExceptionHandler.format( bex, logger );
        }

    }

}