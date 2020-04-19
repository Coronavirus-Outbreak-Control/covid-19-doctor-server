package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
public class EmptyAuthorization extends BaseValidatorsExceptions {
    public EmptyAuthorization( String message ) {
        super( message, Response.Status.FORBIDDEN.getStatusCode() );
    }

    public EmptyAuthorization( Throwable ex ) {
        super( ex, Response.Status.FORBIDDEN.getStatusCode() );
    }

    public EmptyAuthorization() {
        super( Response.Status.FORBIDDEN.getReasonPhrase(), Response.Status.FORBIDDEN.getStatusCode() );
    }

}
