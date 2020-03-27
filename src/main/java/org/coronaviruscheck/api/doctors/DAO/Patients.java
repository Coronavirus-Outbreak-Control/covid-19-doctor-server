package org.coronaviruscheck.api.doctors.DAO;

import org.coronaviruscheck.api.doctors.DAO.Exceptions.NotFoundException;
import org.coronaviruscheck.api.doctors.DAO.POJO.Patient;

import java.sql.SQLException;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 25/03/2020
 */
public class Patients extends AbstractDao {

    public Patients() {
    }

    public static Patient getPatientById( Integer patientId ) throws SQLException, NotFoundException {
        String myQuery = "SELECT * FROM devices_hs_id WHERE id = ?";
        return get( Patient.class, myQuery, patientId );
    }

}
