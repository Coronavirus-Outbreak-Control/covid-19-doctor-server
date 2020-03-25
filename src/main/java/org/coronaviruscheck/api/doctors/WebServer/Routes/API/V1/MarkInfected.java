package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.domain.JWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus;
import org.coronaviruscheck.api.doctors.DAO.POJO.PatientStatus;
import org.coronaviruscheck.api.doctors.DAO.PatientStatuses;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.EmptyAuthorization;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.JwtAuthValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.HashMap;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
@Path("/v1")
public class MarkInfected {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @POST
    @Path("/mark/{patientId}/{newStatus}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response mark(
            HashMap<String, String> payload,
            @PathParam("patientId") Integer patientId,
            @PathParam("newStatus") Integer newStatus,
            @HeaderParam("authorization") String authString
    ) {

        JWT jwt;
        try {
            jwt = JwtAuthValidator.validate( authString );
        } catch ( JWTExpiredException | EmptyAuthorization je ) {
            GenericResponse genericResponse = new GenericResponse();
            genericResponse.message = "Token expired.";
            genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
            return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();
        }

        PatientStatus updatedStatus = null;
        try {
            updatedStatus = this.execute( newStatus, patientId, jwt.getInteger( "id" ) );
        } catch ( SQLException e ) { //SQLException
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        }

        return Response.ok().entity( updatedStatus ).build();

    }

    private PatientStatus execute( Integer newStatus, Integer patientId, Integer doctorId ) throws SQLException {
        try {
            PatientStatus actualStatus = PatientStatuses.getActualStatus( patientId );
            return PatientStatuses.addStatus( actualStatus.getActual_status(), newStatus, patientId, doctorId );
        } catch ( EmptyStackException e ) {
            return PatientStatuses.addStatus( InfectionStatus.NORMAL.toValue(), newStatus, patientId, doctorId );
        }
    }

}
