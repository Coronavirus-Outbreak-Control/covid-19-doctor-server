package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Doctors extends AbstractDao {

    private Doctors() {
    }

    public static void createNew(
            Connection connection,
            String phone_number,
            Integer referred_by
    ) throws SQLException {

        PreparedStatement preparedStmt = connection.prepareStatement(
                "INSERT INTO doctors ( phone_number, active, referred_by ) "
                        + " VALUES( ?,?,? )"
        );

        try {

            // create the mysql insert preparedstatement
            preparedStmt.setString( 1, phone_number );
            preparedStmt.setBoolean( 2, false );
            preparedStmt.setInt( 3, referred_by );

            try {
                preparedStmt.execute();
            } catch ( SQLIntegrityConstraintViolationException ignore ) {
            }

        } finally {
            DbUtils.closeQuietly( preparedStmt );
        }

    }

    public static boolean setActive( String cryptPhoneNumber ) throws SQLException {
        Connection        connection   = null;
        PreparedStatement preparedStmt = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            preparedStmt = connection.prepareStatement(
                    "UPDATE doctors SET active = ?, confirmed_at = NOW() WHERE phone_number = ? and active = 0"
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

    public static Doctor getActiveDoctorByPhone( String cryptPhoneNumber ) throws SQLException, NotFoundException {
        return getDoctorByPhone( cryptPhoneNumber, true );
    }

    public static Doctor getInactiveDoctorByPhone( String cryptPhoneNumber ) throws SQLException, NotFoundException {
        return getDoctorByPhone( cryptPhoneNumber, false );
    }

    public static Doctor getActiveDoctorById( Integer doctorId ) throws SQLException, NotFoundException {
        return getDoctorById( doctorId, true );
    }

    public static Doctor getInactiveDoctorById( Integer doctorId ) throws SQLException, NotFoundException {
        return getDoctorById( doctorId, false );
    }

    protected static Doctor getDoctorById( Integer doctorId, boolean active ) throws SQLException, NotFoundException {

        String myQuery = "SELECT * FROM doctors WHERE id = ?";

        if ( active ) {
            myQuery += " AND active = 1";
        } else {
            myQuery += " AND active = 0";
        }

        return get( Doctor.class, myQuery, doctorId );

    }

    protected static Doctor getDoctorByPhone( String cryptPhoneNumber, boolean active ) throws SQLException, NotFoundException {

        String myQuery = "SELECT * FROM doctors WHERE phone_number = ?";

        if ( active ) {
            myQuery += " AND active = 1";
        } else {
            myQuery += " AND active = 0";
        }

        return get( Doctor.class, myQuery, cryptPhoneNumber );

    }

}
