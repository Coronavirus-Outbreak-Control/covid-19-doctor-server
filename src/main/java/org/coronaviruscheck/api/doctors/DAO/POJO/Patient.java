package org.coronaviruscheck.api.doctors.DAO.POJO;

import java.sql.Timestamp;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 25/03/2020
 */
public class Patient {

    private Integer   id;
    private String    hs_id;
    private String    os_name;
    private String    device_manufacturer;
    private String    device_model;
    private Timestamp created_at;
    private String    notification_id;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getHs_id() {
        return hs_id;
    }

    public void setHs_id( String hs_id ) {
        this.hs_id = hs_id;
    }

    public String getOs_name() {
        return os_name;
    }

    public void setOs_name( String os_name ) {
        this.os_name = os_name;
    }

    public String getDevice_manufacturer() {
        return device_manufacturer;
    }

    public void setDevice_manufacturer( String device_manufacturer ) {
        this.device_manufacturer = device_manufacturer;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model( String device_model ) {
        this.device_model = device_model;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at( Timestamp created_at ) {
        this.created_at = created_at;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id( String notification_id ) {
        this.notification_id = notification_id;
    }
}
