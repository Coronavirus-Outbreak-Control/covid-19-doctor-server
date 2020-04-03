package org.coronaviruscheck.api.doctors.Services.AMQ;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import java.net.URI;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 21/02/2020
 */
public class AMQPooledConnectionSingleton {

    public static volatile PooledConnectionFactory instance = null;

    private AMQPooledConnectionSingleton() {
    }

    public static synchronized void init( URI brokerUrl, String username, String password ) {

        if ( instance == null ) {

            final ActiveMQConnectionFactory mqFactory;
            mqFactory = new ActiveMQConnectionFactory( brokerUrl );

            RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
            queuePolicy.setInitialRedeliveryDelay( 60000 );
            queuePolicy.setRedeliveryDelay( 10000 );
            queuePolicy.setUseExponentialBackOff( false );
            queuePolicy.setMaximumRedeliveries( -1 );
            mqFactory.setRedeliveryPolicy( queuePolicy );

            mqFactory.setUserName( username );
            mqFactory.setPassword( password );

            instance = new PooledConnectionFactory();
            instance.setConnectionFactory( mqFactory );
            instance.setCreateConnectionOnStartup( true );
            instance.setIdleTimeout( 30000 );  // 30 seconds
            instance.setExpiryTimeout( 300000 );  // five minutes
            instance.setMaxConnections( 50 );
            instance.setTimeBetweenExpirationCheckMillis( 30000 );
            instance.setBlockIfSessionPoolIsFull( true );
            instance.setReconnectOnException( true );

            // start connection pool
            instance.start();

        }

    }

}
