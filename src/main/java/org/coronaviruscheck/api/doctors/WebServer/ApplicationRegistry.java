package org.coronaviruscheck.api.doctors.WebServer;

import org.apache.logging.log4j.LogManager;

import java.util.Properties;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 24/02/20
 */
public class ApplicationRegistry {

    private static Properties properties;

    // Find your Account Sid and Auth Token at twilio.com/console
    public static final String ACCOUNT_SID = "AC31711001880b7ddef17744951302ecaa";
    public static final String AUTH_TOKEN  = "13b20bb2907cb0782e0e2bce0c26fec7";

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
    public final static String JWT_SECRET      = properties.getProperty( "JWT_SECRET" );
    public final static String RedisDSN        = properties.getProperty( "Redis.DSN" );
    public final static String MySqlDSN        = properties.getProperty( "MySql.DSN" );
    public final static String MySqlUsername   = properties.getProperty( "MySql.Username" );
    public final static String MySqlPassword   = properties.getProperty( "MySql.Password" );

}
