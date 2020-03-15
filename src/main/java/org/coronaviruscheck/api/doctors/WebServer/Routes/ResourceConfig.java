package org.coronaviruscheck.api.doctors.WebServer.Routes;

import org.coronaviruscheck.api.doctors.WebServer.Routes.API.JsonProcessingExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Created by Domenico Lupinetti
 */
public class ResourceConfig extends org.glassfish.jersey.server.ResourceConfig {

    public ResourceConfig loadRoutes(){

        /*
         * Load additional routes inside the Customers plugins
         */
        for( Class<?> _class : this.packages( "org.coronaviruscheck.api.doctors.WebServer.Routes.API" ).getClasses() ){
            register( _class );
        }

        /*
         * Register common generics application paths
         */
        register( JacksonJaxbJsonProvider.class );
        register( JsonProcessingExceptionMapper.class, 1 );
        return this;

    }

    public ResourceConfig(){}

}
