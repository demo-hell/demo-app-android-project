package br.com.mobicare.cielo.commons.utils.TimeUtilsTest;

import java.util.Calendar;

/**
 * Created by benhur.souza on 07/04/2017.
 */

public class TimeRobot {

    public static long now(){
        return Calendar.getInstance().getTimeInMillis();
    }

    public static long nowAddMinutes(int minutes){
        Calendar tmp =Calendar.getInstance();
        tmp.add(Calendar.MINUTE, -minutes);
        return tmp.getTimeInMillis();
    }

    public static long nowAddDay(int days){
        Calendar tmp = Calendar.getInstance();
        tmp.add(Calendar.HOUR_OF_DAY, -24*days);
        return tmp.getTimeInMillis();
    }

}
