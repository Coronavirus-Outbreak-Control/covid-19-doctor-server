package org.coronaviruscheck.api.doctors.Commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandLineException;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.Command;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.CommandClassFactory;
import org.coronaviruscheck.api.doctors.Services.Notifications;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 03/04/2020
 */
public class PushInfected extends Command implements CommandClassFactory {

    private String device_id;
    private int status;

    public PushInfected( String[] args ) {
        super( args );
        this.appendOptions( this.getOptions() );
    }

    @Override
    protected void appendOptions( Options options ) {
        Option device_id = Option.builder( "d" ).longOpt( "device_id" )
                               .hasArg()
                               .desc( "Set User Device internal id" )
                               .argName( "device_id" )
                               .required()
                               .build();

        Option status = Option.builder( "s" ).longOpt( "status" )
                                    .hasArg()
                                    .desc( "Set the status" )
                                    .argName( "status" )
                                    .required()
                                    .build();

        options.addOption( device_id );
        options.addOption( status );
    }

    @Override
    protected void _execute() throws Exception {
        Notifications not = new Notifications();
        not.pushOnQueue( this.device_id, this.status );
    }

    @Override
    protected void _handleOptions( CommandLine line ) throws CommandLineException {

        if ( line.hasOption( "device_id" ) ) {
            // initialise the member variable
            device_id = line.getOptionValue( "device_id" );
        }

        if ( line.hasOption( "status" ) ) {
            // initialise the member variable
            status = Integer.parseInt( line.getOptionValue( "status" ) );
        }

    }

    @Override
    public Command newInstance( String[] args ) {
        return null;
    }
}
