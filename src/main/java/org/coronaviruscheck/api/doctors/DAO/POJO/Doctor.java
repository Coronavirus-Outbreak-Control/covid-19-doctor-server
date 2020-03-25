package org.coronaviruscheck.api.doctors.DAO.POJO;

import java.sql.Timestamp;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 22/03/2020
 */
public class Doctor {

    private int       id;
    private String    phone_number;
    private String    name;
    private String    surname;
    private int       active;
    private int       referred_by;
    private Timestamp created_at;
    private Timestamp confirmed_at;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number( String phone_number ) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname( String surname ) {
        this.surname = surname;
    }

    public int getActive() {
        return active;
    }

    public void setActive( int active ) {
        this.active = active;
    }

    public int getReferred_by() {
        return referred_by;
    }

    public void setReferred_by( int referred_by ) {
        this.referred_by = referred_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at( Timestamp created_at ) {
        this.created_at = created_at;
    }

    public Timestamp getConfirmed_at() {
        return confirmed_at;
    }

    public void setConfirmed_at( Timestamp confirmed_at ) {
        this.confirmed_at = confirmed_at;
    }

}
