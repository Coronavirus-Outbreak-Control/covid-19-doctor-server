package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.coronaviruscheck.api.doctors.DAO.POJO.Patient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 25/03/2020
 */
public class Patients {

    public Patients() {
    }

    protected static Patient getPatient( String cryptPhoneNumber ) throws SQLException, EmptyStackException {

        String myQuery = "SELECT * FROM devices_hs_id WHERE id = ?";

        QueryRunner   run        = new QueryRunner();
        Connection    connection = null;
        List<Patient> patients;

        try {

            connection = DatabasePool.getDataSource().getConnection();
            ResultSetHandler<List<Patient>> h = new BeanListHandler<>( Patient.class );

            patients = run.query( connection, myQuery, h, cryptPhoneNumber );

            if ( !patients.isEmpty() ) {
                return patients.get( 0 );
            } else {
                throw new EmptyStackException();
            }

        } finally {
            DbUtils.closeQuietly( connection );
        }

    }

}
