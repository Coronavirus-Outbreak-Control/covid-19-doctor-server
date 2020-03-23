package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.coronaviruscheck.api.doctors.DAO.POJO.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Doctors {

    private Doctors() {
    }

    public static void createNew( String phone_number, Integer referred_by, String invitation_token ) throws SQLException {

        Connection        connection   = null;
        PreparedStatement preparedStmt = null;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            preparedStmt = connection.prepareStatement(
                    "INSERT INTO doctors ( phone_number, active, referred_by, invitation_token ) "
                            + " VALUES( ?,?,?,? )"
            );

            // create the mysql insert preparedstatement
            preparedStmt.setString( 1, phone_number );
            preparedStmt.setBoolean( 2, false );
            preparedStmt.setInt( 3, referred_by );
            preparedStmt.setString( 4, invitation_token );

            preparedStmt.execute();

        } finally {
            DbUtils.closeQuietly( preparedStmt );
            DbUtils.closeQuietly( connection );
        }

    }

    public static Doctor getActiveDoctor( String cryptPhoneNumber ) throws SQLException {
        return getDoctor( cryptPhoneNumber, true );
    }

    public static Doctor getInactiveDoctor( String cryptPhoneNumber ) throws SQLException {
        return getDoctor( cryptPhoneNumber, false );
    }

    protected static Doctor getDoctor( String cryptPhoneNumber, boolean active ) throws SQLException {

        String myQuery = "SELECT * FROM doctors WHERE phone_number = ?";

        if( active ){
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
