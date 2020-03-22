package org.coronaviruscheck.api.doctors.WebServer.Routes.API;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/02/2020
 */
@Provider
@Priority(1)
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    @Override
    public Response toResponse( JsonProcessingException e ) {
        GenericResponse clientResponse = new GenericResponse();
        clientResponse.status = Response.Status.BAD_REQUEST.getStatusCode();
        clientResponse.message = e.getMessage();
        return Response.status( Response.Status.BAD_REQUEST ).entity( clientResponse ).build();
    }
}