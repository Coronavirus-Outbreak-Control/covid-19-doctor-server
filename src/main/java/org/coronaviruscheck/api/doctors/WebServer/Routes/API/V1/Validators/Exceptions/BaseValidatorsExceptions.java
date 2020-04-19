package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 19/04/2020
 */
public class BaseValidatorsExceptions extends Exception {

    protected int code;

    protected BaseValidatorsExceptions( Throwable ex, int code ) {
        super( ex.getMessage(), ex );
        this.code = code;
    }

    protected BaseValidatorsExceptions( String message, int code ) {
        super( message );
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public Response toResponse() {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.message = this.getMessage();
        genericResponse.status = code;
        return Response.status( code ).entity( genericResponse ).build();
    }

}
