package org.coronaviruscheck.api.doctors.Services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.CommonLibs.HttpClient.HttpClientAbstract;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;

import static org.coronaviruscheck.api.doctors.CommonLibs.HttpClient.HttpResponse.response;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 27/03/2020
 */
public class Notifications extends HttpClientAbstract {

    private static final String QUEUE_NAME = "doctor-infected";

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LogManager.getLogger( this.getClass() );

    public Notifications() {
    }

    public void callForPushes( String deviceId, Integer status ) {

        try ( CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy( new LaxRedirectStrategy() ).build() ) {

            //GET localhost:9000/push/<device_id>/<status>

            URIBuilder builder = new URIBuilder( "http://localhost:9001/push/" + deviceId + "/" + status );

            HttpGet get = new HttpGet( builder.build() );
            httpclient.execute( get, response() );

        } catch ( Exception ex ) {
            this.logger.error( ex.getMessage(), ex );
        }

    }

    public void pushOnQueue( String deviceId, Integer status ) throws AmazonSQSException {

        final BasicAWSCredentials credentialsProvider = new BasicAWSCredentials(
                ApplicationRegistry.AWS_ACCESS_KEY_ID,
                ApplicationRegistry.AWS_SECRET_ACCESS_KEY
        );

        final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(
                new AWSStaticCredentialsProvider( credentialsProvider )
        ).withRegion( Regions.US_EAST_1 ).build();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put( "device_id", deviceId );
        rootNode.put( "status", status );

        try {
            sqs.createQueue( QUEUE_NAME );
        } catch ( AmazonSQSException e ) {
            if ( !e.getErrorCode().equals( "QueueAlreadyExists" ) ) {
                throw e;
            }
        }

        String queueUrl = sqs.getQueueUrl( QUEUE_NAME ).getQueueUrl();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl( queueUrl )
                .withMessageBody( rootNode.toString() )
                .withDelaySeconds( 5 );

        sqs.sendMessage( send_msg_request );

    }

}
