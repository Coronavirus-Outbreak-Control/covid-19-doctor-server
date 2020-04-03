package org.coronaviruscheck.api.doctors.Services.AMQ;

import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.Services.Redis.RedisHandler;
import org.redisson.api.RAtomicLong;

import javax.jms.*;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/02/2020
 */
public class AMQBrokerAliveCheck {

    private static final String subject = "UGC_PING_QUEUE";

    @SuppressWarnings("DuplicatedCode")
    public static void brokerPing( Logger logger  ) throws Exception {

        Connection      connection = null;
        Session         session    = null;
        MessageProducer producer   = null;

        try {

            RAtomicLong atomicLong = RedisHandler.client.getAtomicLong( "ping" );
            atomicLong.set( 1 );

            connection = AMQPooledConnectionSingleton.instance.createConnection();

            // Getting JMS connection from the server and starting it
            connection.start();
            session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );

            //Destination represents here our queue 'UGC_QUEUE_1' on the JMS server.
            //The queue will be created automatically on the server.
            final Destination destination = session.createQueue( subject );

            // MessageProducer is used for sending messages to the queue.
            producer = session.createProducer( destination );
            producer.setTimeToLive( 2000 );
            producer.setDeliveryMode( DeliveryMode.NON_PERSISTENT );

            TextMessage message = session.createTextMessage( "Ping..." );

            // send message
            producer.send( message );

        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
            throw e;
        } finally {
            AMQonExceptionResourceCleaner.cleanProducerResource( producer, logger );
            AMQonExceptionResourceCleaner.cleanSessionResource( session, logger );
            AMQonExceptionResourceCleaner.cleanConnectionResource( connection,logger );
        }

    }

}
