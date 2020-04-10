package org.coronaviruscheck.api.doctors.Commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandLineException;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.Command;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.CommandClassFactory;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 03/04/2020
 */
public class MarkInfected extends Command implements CommandClassFactory {

    private int patient_id;
    private int doctor_id;
    private int status;

    public MarkInfected( String[] args ) {
        super( args );
        this.appendOptions( this.getOptions() );
    }

    @Override
    protected void appendOptions( Options options ) {
        Option patient_id = Option.builder( "p" ).longOpt( "patient_id" )
                                  .hasArg()
                                  .desc( "Set User Device internal id" )
                                  .argName( "patient_id" )
                                  .required()
                                  .build();

        Option doctor_id = Option.builder( "d" ).longOpt( "doctor_id" )
                                  .hasArg()
                                  .desc( "Set Doctor Device id" )
                                  .argName( "doctor_id" )
                                  .required()
                                  .build();

        Option status = Option.builder( "s" ).longOpt( "status" )
                              .hasArg()
                              .desc( "Set the status" )
                              .argName( "status" )
                              .required()
                              .build();

        options.addOption( patient_id );
        options.addOption( doctor_id );
        options.addOption( status );
    }

    @Override
    protected void _execute() throws Exception {
        org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.MarkInfected not
                = new org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.MarkInfected();
        not.execute( this.status, this.patient_id, this.doctor_id, true );
    }

    @Override
    protected void _handleOptions( CommandLine line ) throws CommandLineException {

        if ( line.hasOption( "patient_id" ) ) {
            // initialise the member variable
            patient_id = Integer.parseInt( line.getOptionValue( "patient_id" ) );
        }

        if ( line.hasOption( "doctor_id" ) ) {
            // initialise the member variable
            doctor_id = Integer.parseInt( line.getOptionValue( "doctor_id" ) );
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
