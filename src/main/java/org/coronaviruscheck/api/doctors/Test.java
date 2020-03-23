package org.coronaviruscheck.api.doctors;

import org.apache.commons.codec.digest.DigestUtils;
import org.coronaviruscheck.api.doctors.CommonLibs.Crypt.Crypto;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class Test {
    public static void main( String[] args ) {
        for ( int i = 0; i < 10; i++ ) {

            Crypto x = new Crypto();
            String sha256hex = x.sha256( "+393920381849", ApplicationRegistry.JWT_SECRET );
            System.out.println( sha256hex );

        }
    }

}
