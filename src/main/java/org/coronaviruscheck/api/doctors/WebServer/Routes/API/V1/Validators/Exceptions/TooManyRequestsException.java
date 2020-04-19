package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 19/04/2020
 */
public class TooManyRequestsException extends BaseValidatorsExceptions {
    public TooManyRequestsException( String message ) {
        super( message, Response.Status.TOO_MANY_REQUESTS.getStatusCode() );
    }

    public TooManyRequestsException( Throwable ex ) {
        super( ex, Response.Status.TOO_MANY_REQUESTS.getStatusCode() );
    }

    public TooManyRequestsException() {
        super( Response.Status.TOO_MANY_REQUESTS.getReasonPhrase(), Response.Status.TOO_MANY_REQUESTS.getStatusCode() );
    }

}
