package br.com.mobicare.cielo.commons.utils;

import java.util.Calendar;

import br.com.mobicare.cielo.BuildConfig;

/**
 * Created by benhur.souza on 04/04/2017.
 */

public final class TimeUtils {

    private TimeUtils(){}

    /**
     * Veriica se já passou 10 minutos(Debug) ou 1 dia
     *
     * @param time
     * @return
     */
    public static boolean needSync(long time){
        Calendar today = Calendar.getInstance();
        if(time == 0){
            return true;
        }

        long diff = today.getTimeInMillis() - time;
        long min = diff / ( 60 * 1000);
//        long hours = diff/ (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        boolean validad = false;

        if(BuildConfig.DEBUG){
            //Quando for em modo debug atualiza de 10 em 10 minutos
            validad = (min >= 10);
        }else{
            validad = (days >= 1);
        }

        return validad;
    }

    /**
     * Retorna a data local
     * @return Long
     */
    public static long getCurrentTime(){
        Calendar today = Calendar.getInstance();
        return today.getTimeInMillis();
    }

    //Transforma o dado do backend para exibir no canal horas ou minutos
    public static String convertMinutesToHours(int timeInMinutes) {
        StringBuilder sb = new StringBuilder();
        if (timeInMinutes > 60) {
            int expirationInHours = timeInMinutes / 60;

            if (expirationInHours > 1) {
                sb.append(expirationInHours).append(" horas");
            } else {
                sb.append(expirationInHours).append(" hora");
            }

            return sb.toString();

        } else {

            if (timeInMinutes > 1) {
                return sb.append(timeInMinutes).append(" minutos").toString();
            }

            return sb.append(timeInMinutes).append(" minuto").toString();
        }

    }

}
