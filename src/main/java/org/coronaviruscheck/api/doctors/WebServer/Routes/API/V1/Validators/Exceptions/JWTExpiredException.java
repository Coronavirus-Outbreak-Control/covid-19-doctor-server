package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 19/04/2020
 */
public class JWTExpiredException extends BaseValidatorsExceptions {
    public JWTExpiredException( String message ) {
        super( message, Response.Status.UNAUTHORIZED.getStatusCode() );
    }

    public JWTExpiredException( Throwable ex ) {
        super( ex, Response.Status.UNAUTHORIZED.getStatusCode() );
    }

    public JWTExpiredException() {
        super( Response.Status.UNAUTHORIZED.getReasonPhrase(), Response.Status.UNAUTHORIZED.getStatusCode() );
    }

}
