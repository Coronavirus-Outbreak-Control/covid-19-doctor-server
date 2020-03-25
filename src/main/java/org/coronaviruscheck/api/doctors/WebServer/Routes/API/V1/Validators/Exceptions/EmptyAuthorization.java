package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
public class EmptyAuthorization extends Exception {

    private int code;

    public EmptyAuthorization( String message, int code ) {
        super( message );
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
