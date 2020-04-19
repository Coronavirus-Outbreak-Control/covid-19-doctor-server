package org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1;

import io.fusionauth.jwt.domain.JWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coronaviruscheck.api.doctors.DAO.Doctors;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;
import org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus;
import org.coronaviruscheck.api.doctors.DAO.POJO.Patient;
import org.coronaviruscheck.api.doctors.DAO.POJO.PatientStatus;
import org.coronaviruscheck.api.doctors.DAO.PatientStatuses;
import org.coronaviruscheck.api.doctors.DAO.Patients;
import org.coronaviruscheck.api.doctors.Services.Notifications;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.Exceptions.*;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.JwtAuthValidator;
import org.coronaviruscheck.api.doctors.WebServer.Routes.API.V1.Validators.SimpleRateLimiterForInfectionsMarks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;

import static org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus.INFECTED;
import static org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus.SUSPECT;

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
            @HeaderParam("authorization") String authString,
            @DefaultValue("false") @QueryParam("ignore_status_check") Boolean ignore_status_check
    ) {

        JWT           jwt;
        PatientStatus updatedStatus;
        try {
            jwt = JwtAuthValidator.validate( authString );
            SimpleRateLimiterForInfectionsMarks.validate( jwt.getInteger( "id" ) );
            updatedStatus = this.execute( newStatus, patientId, jwt.getInteger( "id" ), ignore_status_check );
        } catch ( SQLException e ) { //SQLException
            logger.error( e.getMessage(), e );
            return Response.status( Response.Status.SERVICE_UNAVAILABLE.getStatusCode() ).build();
        } catch ( NotFoundException |
                InvalidInfectionStatus |
                TooManyRequestsException |
                JWTExpiredException |
                EmptyAuthorization customEx
        ) {
            logger.error( customEx.getMessage(), customEx );
            return customEx.toResponse();
        }

        return Response.ok().entity( updatedStatus ).build();

    }

    public PatientStatus execute(
            Integer newStatus,
            Integer patientId,
            Integer doctorId,
            Boolean ignore_status_check
    ) throws SQLException, InvalidInfectionStatus, NotFoundException {

        Doctor  doc;
        Patient pt;
        try {
            doc = Doctors.getActiveDoctorById( doctorId );
            pt = Patients.getPatientById( patientId );
        } catch ( org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException nfEx ) {
            throw new NotFoundException( "Not Found." );
        }

        InfectionStatus newInfectionStatus = InfectionStatus.forValue( newStatus );

        InfectionStatus actualInfectionStatus;
        try {
            //Not found exception if there are no records for the patient
            PatientStatus actualPatientStatus = PatientStatuses.getActualStatus( patientId );
            actualInfectionStatus = InfectionStatus.forValue( actualPatientStatus.getActual_status() );
        } catch ( org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException e ) {
            actualInfectionStatus = InfectionStatus.NORMAL;
        }

        if ( newInfectionStatus == INFECTED && actualInfectionStatus != SUSPECT && !ignore_status_check ) {
            //actual status must be suspect before to mark as infected
            throw new InvalidInfectionStatus( "Previous SUSPECT status not found." );
        }

        PatientStatus freshNewStatus = PatientStatuses.addStatus( actualInfectionStatus, newInfectionStatus, patientId, doctorId );

        // send status to the queue
        Notifications notifications = new Notifications();
        notifications.pushOnQueue( pt.getId(), newStatus );

        return freshNewStatus;

    }

}
