package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.DAO.POJO.InfectionStatus;
import org.coronaviruscheck.api.doctors.DAO.POJO.PatientStatus;

import java.sql.*;
import java.time.Instant;
import java.util.List;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 25/03/2020
 */
public class PatientStatuses {

    public PatientStatuses() {
    }

    public static PatientStatus getActualStatus( Integer patientId ) throws SQLException, NotFoundException {

        String myQuery = "SELECT p1.* " +
                "FROM patient_statuses p1 " +
                "INNER JOIN ( " +
                "    SELECT MAX(id) as id " +
                "    FROM patient_statuses " +
                "    WHERE patient_id = ? " +
                "    GROUP BY patient_id " +
                ") p2 ON " +
                "    p1.id = p2.id " +
                "    AND patient_id = ?";
        return getPatientStatus( myQuery, patientId, patientId ).get( 0 );

    }

    public static PatientStatus addStatus( InfectionStatus actualStatus, InfectionStatus newStatus, Integer patientId, Integer doctorId ) throws SQLException {

        Connection        connection   = null;
        PreparedStatement preparedStmt = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            preparedStmt = connection.prepareStatement(
                    "INSERT INTO patient_statuses " +
                            "( patient_id, old_status, actual_status, updated_by, updated_at ) "
                            + " VALUES( ?,?,?,?,? )",
                    Statement.RETURN_GENERATED_KEYS
            );

            // create the mysql insert preparedstatement
            preparedStmt.setInt( 1, patientId );
            preparedStmt.setInt( 2, actualStatus.toValue() );
            preparedStmt.setInt( 3, newStatus.toValue() );
            preparedStmt.setInt( 4, doctorId );

            Timestamp now = new Timestamp( Instant.now().getEpochSecond() * 1000 );
            preparedStmt.setTimestamp( 5, now );

            preparedStmt.execute();

            ResultSet rs = preparedStmt.getGeneratedKeys();

            int id = 0;
            if ( rs.next() ) {
                id = rs.getInt( 1 );
            }

            PatientStatus newPStatus = new PatientStatus();
            newPStatus.setId( id );
            newPStatus.setPatient_id( patientId );
            newPStatus.setOld_status( actualStatus.toValue() );
            newPStatus.setActual_status( newStatus.toValue() );
            newPStatus.setUpdated_by( doctorId );
            newPStatus.setUpdated_at( now );

            return newPStatus;

        } finally {
            DbUtils.closeQuietly( preparedStmt );
            DbUtils.closeQuietly( connection );
        }

    }

    public static List<PatientStatus> getSuspectStatusesByDoctorId( int doctorId ) throws SQLException, NotFoundException {
        String myQuery = "SELECT p1.* " +
                "FROM patient_statuses p1 " +
                "INNER JOIN ( " +
                "    SELECT MAX(id) as id " +
                "    FROM patient_statuses " +
                "    WHERE updated_by = ? " +
                "    GROUP BY patient_id " +
                ") p2 ON " +
                "    p1.id = p2.id " +
                "    AND updated_by = ?" +
                "    AND actual_status = ?;";

        return getPatientStatus( myQuery, doctorId, doctorId, InfectionStatus.SUSPECT.toValue() );
    }

    public static PatientStatus getById( int rowId ) throws SQLException, NotFoundException {
        String myQuery = "SELECT * FROM patient_statuses where id = ?";
        return getPatientStatus( myQuery, rowId ).get( 0 );
    }

    private static List<PatientStatus> getPatientStatus( String myQuery, Object... params ) throws SQLException, NotFoundException {

        QueryRunner         run        = new QueryRunner();
        Connection          connection = null;
        List<PatientStatus> patients;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            ResultSetHandler<List<PatientStatus>> h = new BeanListHandler<>( PatientStatus.class );

            patients = run.query( connection, myQuery, h, params );

            if ( !patients.isEmpty() ) {
                return patients;
            } else {
                throw new NotFoundException();
            }

        } finally {
            DbUtils.closeQuietly( connection );
        }

    }

}
