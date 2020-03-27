package org.coronaviruscheck.api.doctors.CommonLibs.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.StringWriter;

public class JsonObject {

    @Override
    public String toString() {

        try {
            StringWriter stringWriter = new StringWriter();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
            objectMapper.writeValue( stringWriter, this );
            return stringWriter.toString();
        } catch ( IOException ex ) {
            LogManager.getLogger( getClass() ).error( ex.getMessage(), ex );
            return super.toString();
        }

    }

}
