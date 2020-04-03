package org.coronaviruscheck.api.doctors;

import org.coronaviruscheck.api.doctors.CommandLine.Tasks.CommandRegistry;
import org.coronaviruscheck.api.doctors.Commands.InsertNewDoctor;
import org.coronaviruscheck.api.doctors.Commands.PushInfected;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class Console extends org.coronaviruscheck.api.doctors.CommandLine.Console {

    public static void main( String[] args ) throws Exception {
        Console console = new Console();
        console.run( args );
        System.exit( 0 );
    }

    @Override
    public void registerCommands() {

        //java -cp target/Covid19-DoctorAppServer-1.0.jar org.coronaviruscheck.api.doctors.Console insertNewDoctor -i 0 -p <+phone_number>
        CommandRegistry.getInstance().addCommand( "insertNewDoctor", InsertNewDoctor::new );

        //java -cp target/Covid19-DoctorAppServer-1.0.jar org.coronaviruscheck.api.doctors.Console pushInfected -d 0 -s 0
        CommandRegistry.getInstance().addCommand( "pushInfected", PushInfected::new );

    }

}
