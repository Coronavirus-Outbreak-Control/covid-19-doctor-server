package org.coronaviruscheck.api.doctors.DAO.Exceptions;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 27/03/2020
 */
public class NotFoundException extends Exception {

    public NotFoundException() {
    }

    public NotFoundException( String message ) {
        super( message );
    }

    public NotFoundException( String message, Throwable cause ) {
        super( message, cause );
    }
}
