package org.coronaviruscheck.api.doctors.CommonLibs.HttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    public static ResponseHandler<String> response() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if( status >= 200 && status < 300 ) {
                HttpEntity resp_entity = response.getEntity();

                String resp = EntityUtils.toString( resp_entity, StandardCharsets.UTF_8 );
//                Main.logger.debug( resp );

                return resp;

            } else {
                HttpEntity resp_entity = response.getEntity();
                String     resp        = EntityUtils.toString( resp_entity, StandardCharsets.UTF_8 );
                throw new HttpResponseException( status, resp );
            }
        };
    }

}
