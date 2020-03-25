package org.coronaviruscheck.api.doctors.CommonLibs.Twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class SMS {

    public Logger logger = LogManager.getLogger( this.getClass() );

    public SMS() {
    }

    public void sendMessage( String phone_number, String message ) throws TwilioException {

        Twilio.init( ApplicationRegistry.ACCOUNT_SID, ApplicationRegistry.AUTH_TOKEN );

        Message msg = Message
                .creator(
                        new PhoneNumber( phone_number ), // to
                        new PhoneNumber( "+442033896187" ), // from
                        message
                )
                .create();

        logger.debug( msg );

        switch ( msg.getStatus() ) {
            case FAILED:
            case UNDELIVERED:
                throw new TwilioException( msg.getErrorMessage(), msg.getErrorCode() );
            default:
                break;
        }

    }

}
