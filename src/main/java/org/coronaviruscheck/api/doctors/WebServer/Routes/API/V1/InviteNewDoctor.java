package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.Crypt.Crypto;
import org.coronaviruscheck.api.doctors.CommonLibs.RedisHandler;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Twilio.SMS;
import org.coronaviruscheck.api.doctors.WebServer.Twilio.TwilioException;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Objects;
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

            // If no authorization information present; block access
            if ( authString == null || authString.isEmpty() ) {
                GenericResponse genericResponse = new GenericResponse();
                genericResponse.message = "Authorization required.";
                genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
                return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();
            }

            // Build an HMC verifier using the same secret that was used to sign the JWT
            Verifier verifier = HMACVerifier.newVerifier( ApplicationRegistry.JWT_SECRET );

            // Verify and decode the encoded string JWT to a rich object
            JWT jwt = JWT.getDecoder().decode( authString.substring( 7 ), verifier );

            SMS sms = new SMS();
            sms.sendMessage(
                    payload.get( "phone_number" ),
                    "Please download the app from the store."
            );

            Crypto crypto = new Crypto();
            String token  = crypto.sha256( payload.get( "phone_number" ), ApplicationRegistry.JWT_SECRET );
            String id     = Doctors.createNew( token, Objects.requireNonNull( jwt.getInteger( "id" ) ) );

            RAtomicLong atomicLong = RedisHandler.client.getAtomicLong( "doc_sequence" );
            String      seq        = String.valueOf( atomicLong.incrementAndGet() );
            atomicLong.expire( 1, TimeUnit.SECONDS );

            if ( id.length() > 4 ) {
                id = id.substring( id.length() - 4 );
            }

            id = StringUtils.leftPad( id, 4, "0" );
            seq = StringUtils.leftPad( seq, 2, "0" );

            String authKey = id + seq;

            RBucket<String> authKeyBucket    = RedisHandler.client.getBucket( token );
            RBucket<String> reverseKeyBucket = RedisHandler.client.getBucket( authKey );
            authKeyBucket.set( authKey );
            reverseKeyBucket.set( token );
            authKeyBucket.expire( 60 * 60, TimeUnit.SECONDS );
            reverseKeyBucket.expire( 60 * 60, TimeUnit.SECONDS );

        } catch ( TwilioException e ) {

            logger.error( e.getMessage(), e );

            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = e.getMessage();
            genericResponse.status = e.getCode();

            return Response.status(
                    Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
            ).entity( genericResponse ).build();

        } catch ( JWTExpiredException je ) {

            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = "Token expired.";
            genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
            return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();

        } catch ( NullPointerException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.BAD_REQUEST.getStatusCode() ).build();
        } catch ( Exception e ) { //SQLException and CryptoException also
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

        return Response.ok().build();

    }

}
