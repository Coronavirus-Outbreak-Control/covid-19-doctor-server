package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators;

import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.apache.logging.log4j.LogManager;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.EmptyAuthorization;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.JWTExpiredException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
public class JwtAuthValidator {

    // HeaderParam("authorization") -> "Bearer xxx"
    public static JWT validate( String authString ) throws EmptyAuthorization, JWTExpiredException {

        // If no authorization information present; block access
        if ( authString == null || authString.isEmpty() ) {
            throw new EmptyAuthorization( "Authorization required." );
        }

        // Build an HMC verifier using the same secret that was used to sign the JWT
        Verifier verifier = HMACVerifier.newVerifier( ApplicationRegistry.JWT_SECRET );

        try {
            // Verify and decode the encoded string JWT to a rich object
            return JWT.getDecoder().decode( authString.substring( 7 ), verifier );
        } catch( io.fusionauth.jwt.JWTExpiredException jEEx){
            LogManager.getLogger( JwtAuthValidator.class ).error( jEEx.getMessage(), jEEx );
            throw new JWTExpiredException( "Invalid token." );
        }

    }

}
