package org.coronaviruscheck.api.doctors;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.BootstrapLogger;
import org.coronaviruscheck.api.doctors.Services.AMQ.AMQPooledConnectionSingleton;
import org.coronaviruscheck.api.doctors.Services.Redis.RedisHandler;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Routes.ResourceConfig;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 15/03/2020
 */
public class Main {

    private final CommandLine    commandLine;
    private       ResourceConfig serviceConfig;
    private       Timer          memoryCheck;

    private static final ReentrantReadWriteLock shutDownLock = new ReentrantReadWriteLock();
    private              NioServerSocketChannel nettyServer;

    public static boolean getResourceLock() {
        return shutDownLock.readLock().tryLock();
    }

    public static void releaseResourceLock() {
        shutDownLock.readLock().unlock();
    }

    public static void main( String[] args ) {
        Main _mainClass;
        try {
            _mainClass = new Main( new ResourceConfig(), args );
            _mainClass.run();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private CommandLine parseArgs( String[] args ) {

        CommandLine line    = null;
        Options     options = new Options();

        options.addOption( "p", "port", true, "Force start WebServer on the specified port." );
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            line = parser.parse( options, args );
        } catch ( ParseException e ) {
            logger.error( e.getMessage(), e );
        }

        return line;

    }

    public static final Logger logger   = LogManager.getLogger( Main.class );
    public static final String hostName = getHostName();

    private Main( ResourceConfig serviceConfig, String[] args ) throws URISyntaxException {
        this.commandLine = this.parseArgs( args );
        this.registerShutdownHook();
        RedisHandler.init( ApplicationRegistry.RedisDSN );
//        AMQPooledConnectionSingleton.init(
//                new URI( ApplicationRegistry.BrokerUrl ),
//                ApplicationRegistry.BrokerUser,
//                ApplicationRegistry.BrokerPass
//        );
        this.setServiceConfig( serviceConfig );
        this.setMemoryCheck();
    }

    private void registerShutdownHook() {

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {

            shutDownLock.writeLock().lock();

            try {
                logger.info( "Wait for clean shutdown." );
                Thread.sleep( 1000 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            try {
                nettyServer.close();
            } catch ( Exception ignored ) {
                shutDownLock.writeLock().unlock();
            }

            memoryCheck.cancel();

            logger.info( "Clean shutdown performed." );

        } ) );

    }

    private void setMemoryCheck() {
        memoryCheck = new Timer();
        memoryCheck.schedule( new TimerTask() {
            @Override
            public void run() {
                String sb = "Ram: " + ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / ( 1024 * 1024 ) + "MB";
                logger.warn( sb );
            }
        }, 0, 60 * 1000L ); //every 10 seconds
    }

    private void setServiceConfig( ResourceConfig serviceConfig ) {
        this.serviceConfig = serviceConfig.loadRoutes();
    }

    void run() {

        try {

            int port;
            if ( this.commandLine != null && this.commandLine.hasOption( "p" ) ) {
                port = Integer.parseInt( this.commandLine.getOptionValue( "p" ) );
            } else {
                port = Integer.parseInt( ApplicationRegistry.NettyPort );
            }

            nettyServer = (NioServerSocketChannel) NettyHttpContainerProvider.createServer(
                    URI.create( ApplicationRegistry.NettyBind + ":" + port + "/" ),
                    this.serviceConfig,
                    false
            );

            BootstrapLogger.start( logger, port ); //fancy logging

            Thread.currentThread().join();

        } catch ( Exception e ) {

            logger.error( e.getMessage(), e );
        }

    }

    private static String getHostName() {
        String hName = "";
        try {
            hName = InetAddress.getLocalHost().getHostName();
        } catch ( UnknownHostException ignore ) { /* do nothing */ }
        return hName;
    }

}