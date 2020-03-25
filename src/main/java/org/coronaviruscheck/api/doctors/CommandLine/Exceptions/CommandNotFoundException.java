package org.coronaviruscheck.api.doctors.CommandLine.Exceptions;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 22/07/19
 */
public class CommandNotFoundException extends CommandLineException {

    public CommandNotFoundException( String message ){
        super( message );
    }

}
