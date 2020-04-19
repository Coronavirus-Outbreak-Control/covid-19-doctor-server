package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators;

import org.coronaviruscheck.api.doctors.Services.Redis.RedisHandler;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.TooManyRequestsException;
import org.redisson.api.RAtomicLong;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 19/04/2020
 */
public class SimpleRateLimiterForInfectionsMarks {

    /*
        TODO next step interface for validators and expand/generify the rate limiter concept
     */
    public static void validate( int doctorID) throws TooManyRequestsException {
        String      thisMinute        = Instant.now().atZone( ZoneId.of( "UTC" ) ).format( DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm" ) );
        RAtomicLong triesInThisMinute = RedisHandler.client.getAtomicLong( "mk-" + doctorID + "-" + thisMinute );
        long        tries             = triesInThisMinute.incrementAndGet();

        Calendar c = Calendar.getInstance();
        c.add( Calendar.MINUTE, 1 );
        c.set( Calendar.SECOND, 0 );
        c.set( Calendar.MILLISECOND, 0 );
        long howMany = ( (int) ( c.getTimeInMillis() / 1000 ) - (int) ( System.currentTimeMillis() / 1000 ) );
        if( howMany <= 0){
            howMany = 1;
        }
        triesInThisMinute.expire( howMany, TimeUnit.SECONDS );

        if( tries > 3 ){
            throw new TooManyRequestsException( "Too many tries in this minute." );
        }

    }

}
