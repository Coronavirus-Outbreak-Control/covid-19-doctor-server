package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.domain.JWT;
import org.apache.commons.dbutils.DbUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.Crypt.Crypto;
import org.coronaviruscheck.api.doctors.Services.Redis.RedisHandler;
import org.coronaviruscheck.api.doctors.CommonLibs.Twilio.SMS;
import org.coronaviruscheck.api.doctors.CommonLibs.Twilio.TwilioException;
import org.coronaviruscheck.api.doctors.DAO.DatabasePool;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.EmptyAuthorization;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.JwtAuthValidator;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
@Path("/v1")
public class InviteNewDoctor {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @POST
    @Path("/activation/invite")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run( HashMap<String, String> payload, @HeaderParam("authorization") String authString ) {

        try {

            JWT jwt = JwtAuthValidator.validate( authString );
            this.execute( payload.get( "phone_number" ), jwt.getInteger( "id" ) );

        } catch ( TwilioException e ) {

            logger.error( e.getMessage(), e );

            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = e.getMessage();
            genericResponse.status = e.getCode();

            return Response.status(
                    Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
            ).entity( genericResponse ).build();

        } catch ( JWTExpiredException | EmptyAuthorization je ) {

            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = "Token expired.";
            genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
            return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();

        } catch ( NullPointerException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.BAD_REQUEST.getStatusCode() ).build();
        } catch ( Exception e ) { // | SQLException
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

        return Response.ok().build();

    }

    public void execute( String phone_number, int user_id ) throws Exception {

        String token = Crypto.sha256( phone_number, ApplicationRegistry.JWT_SECRET );

        Connection connection = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            connection.setAutoCommit( false );

            Doctors.createNew( connection, token, user_id );

            String       today = Instant.now().atZone( ZoneId.of( "UTC" ) ).format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
            RSet<String> set   = RedisHandler.client.getSet( "act_code-" + today );

            String authKey = this.findFreeRandom( set );

            RBucket<String> authKeyBucket    = RedisHandler.client.getBucket( token );
            RBucket<String> reverseKeyBucket = RedisHandler.client.getBucket( authKey );
            authKeyBucket.set( authKey );
            reverseKeyBucket.set( token );
            authKeyBucket.expire( 24 * 60 * 60, TimeUnit.SECONDS );
            reverseKeyBucket.expire( 24 * 60 * 60, TimeUnit.SECONDS );

            SMS sms = new SMS();
            sms.sendMessage(
                    phone_number,
                    "You have been invited to join Coronavirus Outbreak Control. Please download the app from the store."
            );

            connection.commit();

        } catch ( Exception e ){
            if ( connection != null ) {
                connection.rollback();
            }
            throw e;
        } finally {
            DbUtils.closeQuietly( connection );
        }

    }

    private String findFreeRandom( RSet<String> set ) throws Exception {

        int cntLoop = 0;
        int digits  = 6;
        int MaxNum  = 1000;
        while ( true ) {

            String code = Crypto.getRandomLeftPaddedDigits( digits );

            if ( set.add( code ) ) {
                Calendar c = Calendar.getInstance();
                c.add( Calendar.DAY_OF_MONTH, 1 );
                c.set( Calendar.HOUR_OF_DAY, 0 );
                c.set( Calendar.MINUTE, 0 );
                c.set( Calendar.SECOND, 0 );
                c.set( Calendar.MILLISECOND, 0 );
                long howMany = ( (int) ( c.getTimeInMillis() / 1000 ) - (int) ( System.currentTimeMillis() / 1000 ) );
                set.expire( howMany, TimeUnit.SECONDS );
                return code;
            }

            cntLoop++;
            if ( cntLoop == MaxNum ) {
                //too much cycles try next time
                throw new Exception( "Too much loops" );
            }

        }

    }

}
