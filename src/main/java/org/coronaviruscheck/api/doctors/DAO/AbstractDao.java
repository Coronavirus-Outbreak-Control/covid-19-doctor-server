package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 27/03/2020
 */
public class AbstractDao {

    protected static <T> T get( Class<T> fetchObj, String query, Object obj ) throws SQLException, NotFoundException {

        QueryRunner  run        = new QueryRunner();
        Connection   connection = null;
        List<T> doctors;

        try {
            connection = DatabasePool.getDataSource().getConnection();
            ResultSetHandler<List<T>> h = new BeanListHandler<>( fetchObj );

            doctors = run.query( connection, query, h, obj );

            if ( !doctors.isEmpty() ) {
                return doctors.get( 0 );
            } else {
                throw new NotFoundException();
            }

        } finally {
            DbUtils.closeQuietly( connection );
        }

    }

}
