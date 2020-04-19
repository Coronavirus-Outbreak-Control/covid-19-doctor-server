package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

import javax.ws.rs.core.Response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 05/04/2020
 */
public class InvalidInfectionStatus extends BaseValidatorsExceptions {

    public InvalidInfectionStatus( Throwable ex ) {
        super( ex, Response.Status.BAD_REQUEST.getStatusCode() );
    }

    public InvalidInfectionStatus( String message ) {
        super( message, Response.Status.BAD_REQUEST.getStatusCode() );
    }

    public InvalidInfectionStatus() {
        super( Response.Status.BAD_REQUEST.getReasonPhrase(), Response.Status.BAD_REQUEST.getStatusCode() );
    }

}
