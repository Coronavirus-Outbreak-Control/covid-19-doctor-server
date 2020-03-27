package org.coronaviruscheck.api.doctors.CommonLibs.HttpClient;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> on 21/01/19
 */
public abstract class HttpClientAbstract {

    protected final static String UserAgent = _getUserAgent();

    private static String _getUserAgent() {

        String userAgent = "";

        try {

            if( UserAgent == null || UserAgent.isEmpty() ) {
                final Properties properties = new Properties();
                properties.load( HttpClientAbstract.class.getResourceAsStream( "/project.properties" ) );
                userAgent = properties.getProperty( "UserAgent" );
            }

        } catch ( Exception ex ) {

            System.out.println( ex.getMessage() );
            ex.printStackTrace();

        }

        return userAgent;
    }

    @NotNull
    public BufferedInputStream getBufferedInputStream( CloseableHttpClient httpclient, HttpGet httpGet ) throws IOException {

        CloseableHttpResponse response = this.getResponse( httpclient, httpGet );

        InputStream bis = response.getEntity().getContent();

//            String filePath = "test.xlf";
//            BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( new File( filePath ) ) );
//            int inByte;
//            while ( ( inByte = bis.read() ) != -1 ) bos.write( inByte );
//            bis.close();
//            bos.close();
//            System.exit( 1 );

        return new BufferedInputStream( bis );

    }

    @NotNull
    public BufferedInputStream getBufferedInputStream( CloseableHttpResponse response ) throws IOException {

        InputStream bis = response.getEntity().getContent();

//            String filePath = "test.xlf";
//            BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( new File( filePath ) ) );
//            int inByte;
//            while ( ( inByte = bis.read() ) != -1 ) bos.write( inByte );
//            bis.close();
//            bos.close();
//            System.exit( 1 );

        return new BufferedInputStream( bis );

    }

    @NotNull
    public CloseableHttpResponse getResponse( CloseableHttpClient httpclient, HttpGet httpGet ) throws IOException {

        CloseableHttpResponse response = httpclient.execute( httpGet );

        int status = response.getStatusLine().getStatusCode();
        if( status >= 200 && status < 300 ) {
            return response;
        } else {
            throw new ClientProtocolException( "Unexpected data status: " + status );
        }

    }

}
