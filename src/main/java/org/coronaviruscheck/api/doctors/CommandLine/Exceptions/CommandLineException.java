package org.coronaviruscheck.api.doctors.CommandLine.Exceptions;

import org.apache.commons.cli.ParseException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 22/07/19
 */
public class CommandLineException extends ParseException {

    public CommandLineException( String message ){
        super( message );
    }

    public CommandLineException( Throwable e ){
        super( e.getMessage() );
    }

}
