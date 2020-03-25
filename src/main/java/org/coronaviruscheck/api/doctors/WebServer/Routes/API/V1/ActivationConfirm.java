package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.RedisHandler;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.ActivationConfirmResponse;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EmptyStackException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
@Path("/v1")
public class ActivationConfirm {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @GET
    @Path("/activation/confirm/{authKeyBySms}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run( @PathParam("authKeyBySms") String authKeyBySms ) {

        RBucket<String> reverseKeyBucket = RedisHandler.client.getBucket( authKeyBySms );
        String          phoneNumber      = reverseKeyBucket.get();

        try {

            Doctor  doc    = Doctors.getInactiveDoctorByPhone( phoneNumber );
            boolean result = Doctors.setActive( doc );

            Signer signer = HMACSigner.newSHA256Signer( ApplicationRegistry.JWT_SECRET );

            JWT jwt = new JWT().setExpiration( ZonedDateTime.now( ZoneOffset.UTC ).plusHours( 24 ) );
            jwt.addClaim( "id", doc.getId() );
            String jwToken = JWT.getEncoder().encode( jwt, signer );

            ActivationConfirmResponse clientResponse = new ActivationConfirmResponse();
            clientResponse.id = doc.getId();
            clientResponse.token = jwToken;

            String       today = Instant.now().atZone( ZoneId.of( "UTC" ) ).format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
            RSet<String> set   = RedisHandler.client.getSet( "act_code-" + today );
            set.remove( authKeyBySms );

            return Response.ok( clientResponse ).build();

        } catch ( EmptyStackException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.NOT_FOUND.getStatusCode() ).build();
        } catch ( SQLException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

    }

}
