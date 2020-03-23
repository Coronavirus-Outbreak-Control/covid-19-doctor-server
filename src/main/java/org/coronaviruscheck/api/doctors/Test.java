package org.coronaviruscheck.api.doctors;

import org.apache.commons.lang3.StringUtils;
import org.coronaviruscheck.api.doctors.DAO.Doctors;

import java.sql.SQLException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 23/03/2020
 */
public class Test {
    public static void main( String[] args ) throws SQLException {

//        String id = Doctors.createNew( "hash", 1 );
        String id = "56358571";
        if ( id == null ) {
            System.out.println( "diocane" );
        } else {

            if ( id.length() > 4 ) {
                id = id.substring( id.length() - 4 );
            }

            System.out.println( StringUtils.leftPad( id, 4, "0" ) );
        }
    }

}
