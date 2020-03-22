package org.coronaviruscheck.api.doctors.CommonLibs;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Crypto {

    public String encrypt( String toBeEncrypted, String password ) throws Exception {

        byte[]          iv      = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivBytes = new IvParameterSpec( iv );

        SecureRandom random    = new SecureRandom();
        byte[]       saltBytes = new byte[ 20 ];
        random.nextBytes( saltBytes );

        SecretKeyFactory factory   = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        KeySpec          spec      = new PBEKeySpec( password.toCharArray(), saltBytes, 65536, 128 );
        SecretKey        tmp       = factory.generateSecret( spec );
        SecretKeySpec    secretKey = new SecretKeySpec( tmp.getEncoded(), "AES" );

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, secretKey, ivBytes );
        byte[] encryptedTextBytes = cipher.doFinal( toBeEncrypted.getBytes( StandardCharsets.UTF_8 ) );

        String b64text = Base64.getEncoder().encodeToString( encryptedTextBytes );

        //prepend salt and vi
        byte[] buffer = new byte[ saltBytes.length + iv.length + encryptedTextBytes.length ];
        System.arraycopy( saltBytes, 0, buffer, 0, saltBytes.length );
        System.arraycopy( iv, 0, buffer, saltBytes.length, iv.length );
        System.arraycopy( encryptedTextBytes, 0, buffer, saltBytes.length + iv.length, encryptedTextBytes.length );

        return Base64.getEncoder().encodeToString( buffer );

    }

    public String decrypt( String encryptedText, String password ) throws Exception {

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );

        //strip off the salt and iv
        ByteBuffer buffer    = ByteBuffer.wrap( Base64.getDecoder().decode( encryptedText ) );

        byte[]     saltBytes = new byte[ 20 ];
        buffer.get( saltBytes, 0, saltBytes.length );

        byte[] ivBytes1 = new byte[ cipher.getBlockSize() ];
        buffer.get( ivBytes1, 0, ivBytes1.length );

        byte[] encryptedTextBytes = new byte[ buffer.capacity() - saltBytes.length - ivBytes1.length ];

        buffer.get( encryptedTextBytes );

        byte[]          iv     = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec( iv );

        SecretKeyFactory factory   = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        KeySpec          spec      = new PBEKeySpec( password.toCharArray(), saltBytes, 65536, 128 );
        SecretKey        tmp       = factory.generateSecret( spec );
        SecretKeySpec    secretKey = new SecretKeySpec( tmp.getEncoded(), "AES" );

        cipher.init( Cipher.DECRYPT_MODE, secretKey, ivspec );
        return new String( cipher.doFinal( encryptedTextBytes ) );

    }

    public int getRandomInt() {
        double randomDouble = Math.random();
        randomDouble = randomDouble * 100000 + 1;
        return (int) randomDouble;
    }

}
