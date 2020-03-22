package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Doctors {


    private Doctors() {
    }

    public static void createNew( String phone_number, Integer referred_by, Integer invitation_token ) throws SQLException {

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
            preparedStmt.setInt( 4, invitation_token );

            preparedStmt.execute();

        } finally {
            DbUtils.closeQuietly( preparedStmt );
            DbUtils.closeQuietly( connection );
        }

    }

}
