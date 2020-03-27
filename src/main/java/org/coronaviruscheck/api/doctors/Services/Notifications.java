package org.coronaviruscheck.api.doctors.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.HttpClient.HttpClientAbstract;

import java.io.IOException;

import static org.coronaviruscheck.api.doctors.CommonLibs.HttpClient.HttpResponse.response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 27/03/2020
 */
public class Notifications extends HttpClientAbstract {

    private Logger logger = LogManager.getLogger( this.getClass() );

    public Notifications() {
    }

    public void callForPushes( String deviceId, Integer status ){

        try ( CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy( new LaxRedirectStrategy() ).build() ) {

            //GET localhost:9000/push/<device_id>/<status>

            URIBuilder builder = new URIBuilder( "http://localhost:9001/push/" + deviceId + "/" + status );

            HttpGet get = new HttpGet( builder.build() );
            httpclient.execute( get, response() );

        } catch ( Exception ex ) {
            this.logger.error( ex.getMessage(), ex );
        }

    }

}
