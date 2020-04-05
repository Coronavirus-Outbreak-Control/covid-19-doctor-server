package org.coronaviruscheck.api.doctors.DAO.Exceptions;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 05/04/2020
 */
public class InvalidInfectionStatus extends Exception {

    public InvalidInfectionStatus() {
    }

    public InvalidInfectionStatus( String message ) {
        super( message );
    }

    public InvalidInfectionStatus( String message, Throwable cause ) {
        super( message, cause );
    }

    public InvalidInfectionStatus( Throwable cause ) {
        super( cause );
    }

    public InvalidInfectionStatus( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
