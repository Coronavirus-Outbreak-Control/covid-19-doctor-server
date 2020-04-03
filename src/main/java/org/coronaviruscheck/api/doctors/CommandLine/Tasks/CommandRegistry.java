package org.coronaviruscheck.api.doctors.CommandLine.Tasks;

import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandNotFoundException;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 22/07/19
 */
public class CommandRegistry {

    private static CommandRegistry instance = null;

    private HashMap<String,CommandClassFactory> availableTasks = new HashMap<>();

    private CommandRegistry(){}

    public static synchronized CommandRegistry getInstance(){
        if( instance == null ){
            instance = new CommandRegistry();
        }
        return instance;
    }

    /**
     *
     * @param args
     * @return
     * @throws CommandNotFoundException
     */
    public Command build( String[] args ) throws CommandNotFoundException {

        String _cmd = args[ 0 ];
        if( availableTasks.get( _cmd ) == null ){
            throw new CommandNotFoundException( "Undefined command: " + args[ 0 ] + "\n Available commands: " + availableTasks.keySet() );
        }

        String[] argsList = Arrays.copyOfRange( args, 1, args.length );
        return availableTasks.get( _cmd ).newInstance( argsList );

    }

    public void addCommand( String cmdNameTask, CommandClassFactory _class ){
        this.availableTasks.put( cmdNameTask, _class );
    }

}
