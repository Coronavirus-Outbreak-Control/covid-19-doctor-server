package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.Main;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
@Path("/v1")
public class ActivationRequest {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @POST
    @Path("/activation/request")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run( HashMap<String, String> payload ) {

        Signer signer = HMACSigner.newSHA256Signer( ApplicationRegistry.JWT_SECRET );

        Map<String, String> contextClaim = new HashMap<>();
        contextClaim.put( "id_vendor", "1" ); // Code ID

        // Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
        JWT jwt = new JWT().setExpiration( ZonedDateTime.now( ZoneOffset.UTC ).plusMinutes( 10 ) );
        jwt.addClaim( "context", contextClaim );

        // Sign and encode the JWT to a JSON string representation
        String token = JWT.getEncoder().encode( jwt, signer );

        GenericResponse clientResponse = new GenericResponse();
        clientResponse.status = Response.Status.OK.getStatusCode();
        clientResponse.data = 1;
        clientResponse.message = payload.get( "phone_number" );
        return Response.ok( clientResponse ).build();

    }

}
