package org.coronaviruscheck.api.doctors.CommonLibs.POJO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.StringWriter;

public class BaseJsonPOJO {

    @Override
    public String toString() {

        try {
            StringWriter stringRequest = new StringWriter();
            ObjectMapper objectMapper  = new ObjectMapper();
//            objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
            objectMapper.writeValue( stringRequest, this );
            return stringRequest.toString();
        } catch ( IOException ex ) {
            LogManager.getLogger( this.getClass() ).error( ex.getMessage(), ex );
            return super.toString();
        }

    }

}
