package org.coronaviruscheck.api.doctors.CommandLine.Tasks;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandLineException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 22/07/19
 */
public abstract class Command {

    protected final String[] args;
    protected final Logger   logger = LogManager.getLogger( "stdout" );

    protected final Options options = new Options();

    public Options getOptions() {
        return options;
    }

    public Command( String[] args ) {
        this.args = args;
        options.addOption(
                Option.builder( "h" ).longOpt( "help" )
                        .optionalArg( true )
                        .hasArg( false )
                        .desc( "Print this message" )
                        .build()
        );
    }

    public static CommandRegistry factory() {
        return CommandRegistry.getInstance();
    }

    public void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( this.getClass().getSimpleName().toLowerCase(), options );
    }

    private void handleOptions( CommandLine line ) throws CommandLineException {
        if ( line.hasOption( "help" ) ) {
            // initialise the member variable
            this.help();
            System.exit( 0 );
        }
        this._handleOptions( line );
    }

    public void execute() throws Exception {
        CommandLine line = this.parseArguments( options, args );
        this.handleOptions( line );
        this._execute();
    }

    private CommandLine parseArguments( Options options, String[] args ) throws CommandLineException {
        CommandLine line;
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            line = parser.parse( options, args );
        } catch ( ParseException e ) {
            throw new CommandLineException( e );
        }
        return line;
    }

    protected abstract void appendOptions( Options options );
    protected abstract void _execute() throws Exception;
    protected abstract void _handleOptions( CommandLine line ) throws CommandLineException;

}
