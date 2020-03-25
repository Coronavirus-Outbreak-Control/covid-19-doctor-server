package org.coronaviruscheck.api.doctors.DAO.POJO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Domenico Lupinetti <ostico@gmail.com> - 24/03/2020
 */
public enum InfectionStatus {

    NORMAL, INFECTED, SUSPECT, HEALED, QUARANTINE_LIGHT, QUARANTINE_WARNING, QUARANTINE_ALERT;

    private static Map<Integer, InfectionStatus> namesMap = new HashMap<>();

    static {
        namesMap.put( 0, NORMAL );
        namesMap.put( 1, INFECTED );
        namesMap.put( 2, SUSPECT );
        namesMap.put( 3, HEALED );
        namesMap.put( 4, QUARANTINE_LIGHT );
        namesMap.put( 5, QUARANTINE_WARNING );
        namesMap.put( 6, QUARANTINE_ALERT );
    }

    @JsonCreator
    public static InfectionStatus forValue( Integer value ) {
        return namesMap.get( value );
    }

    @JsonValue
    public int toValue() {
        for ( Map.Entry<Integer, InfectionStatus> entry : namesMap.entrySet() ) {
            if ( entry.getValue() == this ) {
                return entry.getKey();
            }
        }
        return 0;
    }

}
