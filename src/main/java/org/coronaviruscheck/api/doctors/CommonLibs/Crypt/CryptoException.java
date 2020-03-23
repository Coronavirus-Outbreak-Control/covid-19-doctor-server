package org.coronaviruscheck.api.doctors.CommonLibs.Crypt;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class CryptoException extends Exception {
    public CryptoException( Exception e ) {
        super(e);
    }
}
