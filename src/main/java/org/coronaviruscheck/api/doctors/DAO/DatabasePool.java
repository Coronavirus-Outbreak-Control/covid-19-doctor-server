package org.coronaviruscheck.api.doctors.DAO;

import org.apache.commons.dbcp2.BasicDataSource;
import org.coronaviruscheck.api.doctors.WebServer.ApplicationRegistry;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class DatabasePool {

    private static BasicDataSource ds;

    static {
        init();
    }

    protected static void init() {
        if ( ds == null ) {
            synchronized ( DatabasePool.class ) {
                if ( ds == null ) {
                    ds = new BasicDataSource();
                    // Starting with default configuration, let's see how good it is.
                    // Default pool size is 8. Configuration guide: https://goo.gl/qxDjo5
                    ds.setDriverClassName( "com.mysql.cj.jdbc.Driver" );
                    ds.setUrl( ApplicationRegistry.MySqlDSN );
                    ds.setUsername( ApplicationRegistry.MySqlUsername );
                    ds.setPassword( ApplicationRegistry.MySqlPassword );
                    ds.setMaxWaitMillis( 5000 );
                    ds.setMinIdle( 10 );
                    ds.setMaxIdle( 10 );
                    ds.setMaxTotal( 100 );
                    ds.setInitialSize( 10 );
                    ds.setMinEvictableIdleTimeMillis( 30000 );
                }
            }
        }
    }

    public static BasicDataSource getDataSource(){
        return ds;
    }

}
