package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.Crypt.Crypto;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Twilio.SMS;
import org.coronaviruscheck.api.doctors.WebServer.Twilio.TwilioException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.EmptyStackException;
import java.util.HashMap;

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

        Crypto crypto = new Crypto();
        try {
            String token = crypto.sha256( payload.get( "phone_number" ), ApplicationRegistry.JWT_SECRET );

            Doctor doc = Doctors.getInactiveDoctor( token );

            SMS    sms   = new SMS();
            sms.sendMessage( payload.get( "phone_number" ), doc.getInvitation_token() );

        } catch ( TwilioException e ) {

            logger.error( e.getMessage(), e );
            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = e.getMessage();
            genericResponse.status = e.getCode();

            return Response.status(
                    Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
            ).entity( genericResponse ).build();

        } catch ( EmptyStackException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.NOT_FOUND.getStatusCode() ).build();
        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

        return Response.accepted().build();

    }

}
