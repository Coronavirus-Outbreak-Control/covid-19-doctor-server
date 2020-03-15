
package org.coronaviruscheck.api.doctors.WebServer.Routes.API;

import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 04/12/18
 */
public class APIExceptionHandler {

    /**
     * Used to log the exception but to return a JSON custom Error
     *
     * @param message
     * @param status
     * @param e
     * @param logger
     * @return
     */
    public static Response format( String message, Response.Status status, Exception e, Logger logger ) {
        logger.error( message, e );
        GenericResponse clientResponse = new GenericResponse();
        clientResponse.status = status.getStatusCode();
        clientResponse.message = message;
        return respond( clientResponse, status );
    }

    /**
     * Return an exception as JSON object error
     *
     * @param e
     * @param logger
     * @return
     */
    public static Response format( Exception e, Logger logger ) {

        GenericResponse clientResponse = new GenericResponse();
        clientResponse.status = Response.Status.SERVICE_UNAVAILABLE.getStatusCode();
        clientResponse.message = e.getMessage();

        Response.Status responseStatus = Response.Status.SERVICE_UNAVAILABLE;
        logger.error( e.getMessage(), e );

        return respond( clientResponse, responseStatus );
    }

    /**
     * Build JSON Error Object
     *
     * @param clientResponse
     * @param status
     * @return
     */
    protected static Response respond( GenericResponse clientResponse, Response.Status status ) {
        return Response.status( status.getStatusCode() ).entity( clientResponse ).build();
    }

}
