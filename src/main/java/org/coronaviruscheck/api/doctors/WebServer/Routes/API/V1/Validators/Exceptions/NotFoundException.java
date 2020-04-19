package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 19/04/2020
 */
public class NotFoundException extends BaseValidatorsExceptions {

    public NotFoundException( Throwable ex ) {
        super( ex, Response.Status.NOT_FOUND.getStatusCode() );
    }

    public NotFoundException( String message ) {
        super( message, Response.Status.NOT_FOUND.getStatusCode() );
    }

    public NotFoundException() {
        super( Response.Status.NOT_FOUND.getReasonPhrase(), Response.Status.NOT_FOUND.getStatusCode() );
    }

}
