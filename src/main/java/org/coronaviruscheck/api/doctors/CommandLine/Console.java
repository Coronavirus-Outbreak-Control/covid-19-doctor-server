package org.coronaviruscheck.api.doctors.CommandLine;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandLineException;
import org.coronaviruscheck.api.doctors.CommandLine.Exceptions.CommandNotFoundException;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.Command;
import org.coronaviruscheck.api.doctors.CommandLine.Tasks.ConsoleInterface;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 16/07/19
 */
public abstract class Console implements ConsoleInterface {

    public static Logger logger;

    protected Console() {

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel( Level.ERROR );
        builder.setConfigurationName( "CommandLog" );
        builder.add( builder.newFilter( "ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL ).addAttribute( "level", Level.DEBUG ) );

        AppenderComponentBuilder appenderBuilder = builder.newAppender( "stdout", "CONSOLE" ).addAttribute( "target", ConsoleAppender.Target.SYSTEM_OUT );
        appenderBuilder.add( builder.newLayout( "PatternLayout" ).addAttribute( "pattern", "%msg%n%throwable" ) );
        appenderBuilder.add( builder.newFilter( "MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL ).addAttribute( "marker", "FLOW" ) );

        builder.add( appenderBuilder );
        builder.add( builder.newLogger( "org.apache.logging.log4j", Level.DEBUG ).add( builder.newAppenderRef( "stdout" ) ).addAttribute( "additivity", false ) );
        builder.add( builder.newRootLogger( Level.DEBUG ).add( builder.newAppenderRef( "stdout" ) ) );
        Configurator.initialize( builder.build() );

        this.registerCommands();
    }

    protected void run( String[] args ) throws Exception {

        logger = LogManager.getLogger( "stdout" );

        Command cmd = null;

        try {
            cmd = Command.factory().build( args );
        } catch ( CommandNotFoundException cmdNF ) {
            System.err.println( cmdNF.getMessage() );
            System.exit( 1 );
        }

        try {
            cmd.execute();
        } catch ( CommandLineException pEx ) {
            System.err.println( pEx.getMessage() );
            cmd.help();
            System.exit( 1 );
        }

    }

}
