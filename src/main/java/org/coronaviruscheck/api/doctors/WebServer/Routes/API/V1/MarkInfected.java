package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.domain.JWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.InvalidInfectionStatus;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;
import org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus;
import org.coronaviruscheck.api.doctors.DAO.POJO.Patient;
import org.coronaviruscheck.api.doctors.DAO.POJO.PatientStatus;
import org.coronaviruscheck.api.doctors.DAO.PatientStatuses;
import org.coronaviruscheck.api.doctors.DAO.Patients;
import org.coronaviruscheck.api.doctors.Services.Notifications;
import org.coronaviruscheck.api.doctors.WebServer.Responses.GenericResponse;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.EmptyAuthorization;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.JwtAuthValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;

import static org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus.*;

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

        PatientStatus updatedStatus;
        try {
            updatedStatus = this.execute( newStatus, patientId, jwt.getInteger( "id" ) );
        } catch ( NotFoundException e ) {
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.NOT_FOUND.getStatusCode() ).build();
        } catch ( SQLException e ) { //SQLException
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        } catch ( InvalidInfectionStatus invExSt ) {
            logger.error( invExSt.getMessage(), invExSt );
            return Response.status( Response.Status.BAD_REQUEST.getStatusCode() ).build();
        }

        return Response.ok().entity( updatedStatus ).build();

    }

    public PatientStatus execute( Integer newStatus, Integer patientId, Integer doctorId ) throws SQLException, NotFoundException, InvalidInfectionStatus {

        Doctor  doc = Doctors.getActiveDoctorById( doctorId );
        Patient pt  = Patients.getPatientById( patientId );

        InfectionStatus newInfectionStatus = InfectionStatus.forValue( newStatus );

        PatientStatus freshNewStatus;
        try {

            //Not found exception if there are no records for the patient
            PatientStatus actualPatientStatus = PatientStatuses.getActualStatus( patientId );

            InfectionStatus actualInfectionStatus = InfectionStatus.forValue( actualPatientStatus.getActual_status() );
            if ( newInfectionStatus == INFECTED && actualInfectionStatus != SUSPECT ) {
                //actual status must be suspect before to mark as infected
                throw new InvalidInfectionStatus( "Previous SUSPECT status not found." );
            }

            freshNewStatus = PatientStatuses.addStatus( actualInfectionStatus, newInfectionStatus, patientId, doctorId );
        } catch ( NotFoundException e ) {
            freshNewStatus = PatientStatuses.addStatus( InfectionStatus.NORMAL, newInfectionStatus, patientId, doctorId );
        }

        if ( newInfectionStatus != InfectionStatus.SUSPECT ) {
            Notifications notifications = new Notifications();
            notifications.pushOnQueue( pt.getHs_id(), newStatus );
        }

        return freshNewStatus;

    }

}
