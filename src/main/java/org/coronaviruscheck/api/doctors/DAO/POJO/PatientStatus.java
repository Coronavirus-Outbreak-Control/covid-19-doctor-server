package org.coronaviruscheck.api.doctors.DAO.POJO;

import java.sql.Timestamp;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 25/03/2020
 */
public class PatientStatus {

    private int id;
    private int             patient_id;
    private int old_status;
    private int actual_status;
    private int             updated_by;
    private Timestamp       updated_at;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id( int patient_id ) {
        this.patient_id = patient_id;
    }

    public int getOld_status() {
        return old_status;
    }

    public void setOld_status( int old_status ) {
        this.old_status = old_status;
    }

    public int getActual_status() {
        return actual_status;
    }

    public void setActual_status( int actual_status ) {
        this.actual_status = actual_status;
    }

    public int getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by( int updated_by ) {
        this.updated_by = updated_by;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at( Timestamp updated_at ) {
        this.updated_at = updated_at;
    }

}
