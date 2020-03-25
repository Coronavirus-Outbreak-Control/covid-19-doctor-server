package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Responses.RequestTokenResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.EmptyStackException;
import java.util.HashMap;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
@Path("/v1")
public class RefreshToken {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run( HashMap<String, String> payload, @HeaderParam("authorization") String authString ) {

        // If no authorization information present; block access
        if ( authString == null || authString.isEmpty() ) {
            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = "Authorization required.";
            genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
            return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();
        }

        try {

            Doctor doc = Doctors.getActiveDoctorByPhone( authString.substring( 7 ) );

            Signer signer = HMACSigner.newSHA256Signer( ApplicationRegistry.JWT_SECRET );

            JWT jwt = new JWT().setExpiration( ZonedDateTime.now( ZoneOffset.UTC ).plusMinutes( 10 ) );
            jwt.addClaim( "id", doc.getId() );

            // Sign and encode the JWT to a JSON string representation
            String token = JWT.getEncoder().encode( jwt, signer );

            RequestTokenResponse clientResponse = new RequestTokenResponse();
            clientResponse.token = token;

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
