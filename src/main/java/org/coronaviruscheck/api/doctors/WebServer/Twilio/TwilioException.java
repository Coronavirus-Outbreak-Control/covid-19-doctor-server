package org.coronaviruscheck.api.doctors.WebServer.Twilio;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class TwilioException extends Exception {
    private Integer code;

    public TwilioException( String message, Integer code ) {
        super( message );
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode( Integer code ) {
        this.code = code;
    }
}
