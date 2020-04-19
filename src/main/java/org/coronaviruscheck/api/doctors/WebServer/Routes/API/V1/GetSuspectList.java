package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.DAO.PatientStatuses;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.EmptyAuthorization;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.JWTExpiredException;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.JwtAuthValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 05/04/2020
 */
@Path("/v1")
public class GetSuspectList {

    public Logger logger = LogManager.getLogger( this.getClass() );

    @GET
    @Path("/patient/suspect/{by_doctor_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getList(
            HashMap<String, String> payload,
            @PathParam("by_doctor_id") Integer doctorID,
            @HeaderParam("authorization") String authString
    ) {

        GenericResponse genericResponse = new GenericResponse();
        try {
            JwtAuthValidator.validate( authString );
        } catch ( JWTExpiredException | EmptyAuthorization je ) {
            genericResponse.message = "Invalid Token.";
            genericResponse.status = Response.Status.FORBIDDEN.getStatusCode();
            return Response.status( Response.Status.FORBIDDEN.getStatusCode() ).entity( genericResponse ).build();
        }

        try {
            genericResponse.data = PatientStatuses.getSuspectStatusesByDoctorId( doctorID );
            genericResponse.status = Response.Status.OK.getStatusCode();

        } catch ( SQLException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        } catch ( NotFoundException e ) {
            genericResponse.data = new ArrayList<>();  //return empty list
        }

        return Response.status( Response.Status.OK.getStatusCode() ).entity( genericResponse ).build();

    }

}



