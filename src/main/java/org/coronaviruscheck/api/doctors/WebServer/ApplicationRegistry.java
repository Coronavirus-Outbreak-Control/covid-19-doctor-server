package org.coronaviruscheck.api.doctors.WebServer;

import org.apache.logging.log4j.LogManager;

import java.util.Properties;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 24/02/20
 */
public class ApplicationRegistry {

    private static Properties properties;

    static {

        try {
            properties = new Properties();
            properties.load( ApplicationRegistry.class.getResourceAsStream( "/project.properties" ) );

        } catch ( Exception ex ) {
            LogManager.getLogger( ApplicationRegistry.class ).error( ex.getMessage(), ex );
        }

    }

    public final static String Environment     = properties.getProperty( "ENV" ).isEmpty() ? "production" : properties.getProperty( "ENV" );
    public final static String UserAgent       = properties.getProperty( "UserAgent" );
    public final static String Version         = properties.getProperty( "ApplicationVersion" );
    public final static String ApplicationName = properties.getProperty( "ApplicationName" );
    public final static String NettyPort       = properties.getProperty( "Server.Netty.Port" );
    public final static String NettyBind       = properties.getProperty( "Server.Netty.Bind" );

}
