package org.coronaviruscheck.api.doctors.Commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandLineException;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.Command;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.CommandClassFactory;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.InviteNewDoctor;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
public class InsertNewDoctor extends Command implements CommandClassFactory {

    private String user_id;
    private String phone_number;

    public InsertNewDoctor( String[] args ){
        super( args );
        this.appendOptions( this.getOptions() );
    }

    @Override
    protected void appendOptions( Options options ) {
        Option user_id = Option.builder( "i" ).longOpt( "user_id" )
                               .hasArg()
                               .desc( "Add User Referral" )
                               .argName( "user_id" )
                               .required()
                               .build();

        Option phone_number = Option.builder( "p" ).longOpt( "phone_number" )
                              .hasArg()
                              .desc( "Add New Phone Number" )
                              .argName( "phone_number" )
                              .required()
                              .build();

        options.addOption( user_id );
        options.addOption( phone_number );
    }

    @Override
    protected void _execute() throws Exception {
        InviteNewDoctor ind = new InviteNewDoctor();
        ind.execute( this.phone_number, Integer.parseInt( this.user_id ) );
    }

    @Override
    protected void _handleOptions( CommandLine line ) throws CommandLineException {

        if ( line.hasOption( "user_id" ) ) {
            // initialise the member variable
            user_id = line.getOptionValue( "user_id" );
        }

        if ( line.hasOption( "phone_number" ) ) {
            // initialise the member variable
            phone_number = line.getOptionValue( "phone_number" );
        }

    }

    @Override
    public Command newInstance( String[] args ) {
        return new InsertNewDoctor( args );
    }
}
