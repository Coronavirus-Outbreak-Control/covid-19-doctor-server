package org.coronaviruscheck.api.doctors.CommandLine.Exceptions;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 22/07/19
 */
public class InvalidParameterException extends CommandLineException  {

    public InvalidParameterException( String message ){
        super( message );
    }

}
