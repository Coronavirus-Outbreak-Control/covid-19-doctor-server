package org.coronaviruscheck.api.doctors.CommonLibs;

/**
 * Created with IntelliJ IDEA.
 * User: Gr3m0r!4n
 * Date: 21/09/13
 * Time: 17.40
 */
public class Utils {

    public static String secondsToDate( int totalSeconds ) {

        int seconds = totalSeconds % 60; // 13473 % 60 = 33
        int totalMinutes = totalSeconds / 60; // 13473 / 60 = 224,55
        int minutes = totalMinutes % 60;  // 224 % 60 = 44
        int totalHours = totalMinutes / 60;   // 224 / 60 = 3.73
        int hours = totalHours % 24;   // hours more that a day
        int days = totalHours / 24;

        return String.format( "%02d days %02d:%02d:%02d", days, hours, minutes, seconds );
    }

}
