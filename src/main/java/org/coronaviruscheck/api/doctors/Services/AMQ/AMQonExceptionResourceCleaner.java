package org.coronaviruscheck.api.doctors.Services.AMQ;

import org.apache.logging.log4j.Logger;

import javax.jms.*;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/02/2020
 */
public class AMQonExceptionResourceCleaner {

    public static void cleanConnectionResource( Connection connection, Logger logger ) {
        try {
            if ( connection != null ) {
                connection.close();
            }
        } catch ( JMSException cex ) {
            logger.error( cex.getMessage(), cex );
        }
    }

    public static void cleanSessionResource( Session session, Logger logger ){
        try {
            if ( session != null ) {
                if( session.getTransacted() ){
                    session.rollback();
                }
                session.close();
            }
        } catch ( JMSException sex ) {
            logger.error( sex.getMessage(), sex );
        }
    }

    public static void cleanProducerResource( MessageProducer producer, Logger logger ){
        try {
            if ( producer != null ) {
                producer.close();
            }
        } catch ( JMSException pex ) {
            logger.error( pex.getMessage(), pex );
        }
    }

    public static void cleanConsumerResource( MessageConsumer consumer, Logger logger ){
        try {
            if ( consumer != null ) {
                consumer.close();
            }
        } catch ( JMSException cex ) {
            logger.error( cex.getMessage(), cex );
        }
    }

}
