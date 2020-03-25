package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Doctors {

    private Doctors() {
    }

    public static String createNew( String phone_number, Integer referred_by ) throws SQLException {

        Connection        connection   = null;
        PreparedStatement preparedStmt = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            preparedStmt = connection.prepareStatement(
                    "INSERT INTO doctors ( phone_number, active, referred_by ) "
                            + " VALUES( ?,?,? )",
                    Statement.RETURN_GENERATED_KEYS
            );

            // create the mysql insert preparedstatement
            preparedStmt.setString( 1, phone_number );
            preparedStmt.setBoolean( 2, false );
            preparedStmt.setInt( 3, referred_by );

            try {
                preparedStmt.execute();
            } catch ( SQLIntegrityConstraintViolationException e ) {
                Doctor doc = getInactiveDoctorByPhone( phone_number );
                return String.valueOf( doc.getId() );
            }

            ResultSet rs = preparedStmt.getGeneratedKeys();

            String id = null;
            if ( rs.next() ) {
                id = String.valueOf( rs.getLong( 1 ) );
            }

            return id;

        } finally {
            DbUtils.closeQuietly( preparedStmt );
            DbUtils.closeQuietly( connection );
        }

    }

    public static boolean setActive( String cryptPhoneNumber ) throws SQLException {
        Connection        connection   = null;
        PreparedStatement preparedStmt = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            preparedStmt = connection.prepareStatement(
                    "UPDATE doctors SET active = ? WHERE phone_number = ? and active = 0"
            );

            preparedStmt.setInt( 1, 1 );
            preparedStmt.setString( 2, cryptPhoneNumber );

            return preparedStmt.executeUpdate() > 0;

        } finally {
            DbUtils.closeQuietly( preparedStmt );
            DbUtils.closeQuietly( connection );
        }

    }

    public static boolean setActive( Doctor doc ) throws SQLException {
        return setActive( doc.getPhone_number() );
    }

    public static Doctor getActiveDoctorByPhone( String cryptPhoneNumber ) throws SQLException {
        return getDoctor( cryptPhoneNumber, true );
    }

    public static Doctor getInactiveDoctorByPhone( String cryptPhoneNumber ) throws SQLException {
        return getDoctor( cryptPhoneNumber, false );
    }

    protected static Doctor getDoctor( String cryptPhoneNumber, boolean active ) throws SQLException, EmptyStackException {

        String myQuery = "SELECT * FROM doctors WHERE phone_number = ?";

        if ( active ) {
            myQuery += " AND active = 1";
        } else {
            myQuery += " AND active = 0";
        }

        QueryRunner  run        = new QueryRunner();
        Connection   connection = null;
        List<Doctor> doctors;

        try {
            connection = DatabasePool.getDataSource().getConnection();
            ResultSetHandler<List<Doctor>> h = new BeanListHandler<>( Doctor.class );

            doctors = run.query( connection, myQuery, h, cryptPhoneNumber );

            if ( !doctors.isEmpty() ) {
                return doctors.get( 0 );
            } else {
                throw new EmptyStackException();
            }

        } finally {
            DbUtils.closeQuietly( connection );
        }

    }

}
