package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;
import org.coronaviruscheck.api.doctors.WebServer.Crypto;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Twilio.TwilioException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;

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
    public Response run( HashMap<String, String> payload ) {

        try {
//            this.sendMessage( payload.get( "phone_number" ) );
            Crypto crypto = new Crypto();
            String token = crypto.encrypt( payload.get( "phone_number" ),  ApplicationRegistry.JWT_SECRET );
            Doctors.createNew( token, Integer.parseInt( payload.get( "id" ) ) );
        } catch ( TwilioException e ) {

            logger.error( e.getMessage(), e );

            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = "Wrong phone number.";
            genericResponse.status = e.getCode();

            return Response.status(
                    Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
            ).entity( genericResponse ).build();

        } catch ( Exception e ) { //SQLException also
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

        return Response.ok().build();

    }

    protected void sendMessage( String phone_number ) throws TwilioException {

        Twilio.init( ApplicationRegistry.ACCOUNT_SID, ApplicationRegistry.AUTH_TOKEN );

        Message message = Message
                .creator(
                        new PhoneNumber( phone_number ), // to
                        new PhoneNumber( "+442033896187" ), // from
                        "Please download the app from the store."
                )
                .create();

        logger.debug( message );

        switch ( message.getStatus() ) {
            case FAILED:
            case UNDELIVERED:
                throw new TwilioException( message.getErrorMessage(), message.getErrorCode() );
            default:
                break;
        }

    }

}
