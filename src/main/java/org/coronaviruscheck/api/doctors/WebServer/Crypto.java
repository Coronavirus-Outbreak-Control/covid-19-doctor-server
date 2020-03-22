package org.coronaviruscheck.api.doctors.WebServer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Crypto {

    public String encrypt( String toBeEncrypted, String password ) throws Exception {

        byte[] ivBytes;

        SecureRandom random = new SecureRandom();
        byte[]       bytes  = new byte[ 20 ];

        random.nextBytes( bytes );

        // Derive the key
        SecretKeyFactory factory   = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        PBEKeySpec       spec      = new PBEKeySpec( password.toCharArray(), bytes, 65556, 256 );
        SecretKey        secretKey = factory.generateSecret( spec );
        SecretKeySpec    secret    = new SecretKeySpec( secretKey.getEncoded(), "AES" );

        //encrypting the word
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, secret );
        AlgorithmParameters params = cipher.getParameters();
        ivBytes = params.getParameterSpec( IvParameterSpec.class ).getIV();
        byte[] encryptedTextBytes = cipher.doFinal( toBeEncrypted.getBytes( StandardCharsets.UTF_8 ) );

        //prepend salt and vi
        byte[] buffer = new byte[ bytes.length + ivBytes.length + encryptedTextBytes.length ];
        System.arraycopy( bytes, 0, buffer, 0, bytes.length );
        System.arraycopy( ivBytes, 0, buffer, bytes.length, ivBytes.length );
        System.arraycopy( encryptedTextBytes, 0, buffer, bytes.length + ivBytes.length, encryptedTextBytes.length );
        return Base64.getEncoder().encodeToString( buffer );

    }

    public static String decrypt( String encryptedText, String password ) throws Exception {

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );

        //strip off the salt and iv
        ByteBuffer buffer    = ByteBuffer.wrap( Base64.getDecoder().decode( encryptedText ) );
        byte[]     saltBytes = new byte[ 20 ];
        buffer.get( saltBytes, 0, saltBytes.length );
        byte[] ivBytes1 = new byte[ cipher.getBlockSize() ];
        buffer.get( ivBytes1, 0, ivBytes1.length );
        byte[] encryptedTextBytes = new byte[ buffer.capacity() - saltBytes.length - ivBytes1.length ];

        buffer.get( encryptedTextBytes );

        // Deriving the key
        SecretKeyFactory factory   = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        PBEKeySpec       spec      = new PBEKeySpec( password.toCharArray(), saltBytes, 65556, 256 );
        SecretKey        secretKey = factory.generateSecret( spec );
        SecretKeySpec    secret    = new SecretKeySpec( secretKey.getEncoded(), "AES" );
        cipher.init( Cipher.DECRYPT_MODE, secret, new IvParameterSpec( ivBytes1 ) );

        byte[] decryptedTextBytes = null;

        decryptedTextBytes = cipher.doFinal( encryptedTextBytes );

        return new String( Objects.requireNonNull( decryptedTextBytes ) );

    }

}
