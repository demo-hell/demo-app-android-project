package br.com.mobicare.cielo.commons.utils;

import android.os.Build;

import java.security.MessageDigest;

/**
 * Created by benhur.souza on 31/03/2017.
 */

public class DeviceInfo {

    private static class Holder {
        static final DeviceInfo INSTANCE = new DeviceInfo();
    }

    public static DeviceInfo getInstance() {
        return Holder.INSTANCE;
    }

    public String getDeviceId() {
        return encrypt(getPseudoUniqueId());
    }

    private String getPseudoUniqueId() {
        return Build.BOARD + Build.BRAND + Build.CPU_ABI + Build.DEVICE + Build.DISPLAY + Build.HOST + Build.ID + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER;
    }

    private String encrypt(String password) {
        String sign = password;

        try {
            MessageDigest nsae = MessageDigest.getInstance("SHA-1");
            nsae.update(sign.getBytes());
            byte[] hash = nsae.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; ++i) {
                if ((255 & hash[i]) < 16) {
                    hexString.append("0" + Integer.toHexString(255 & hash[i]));
                } else {
                    hexString.append(Integer.toHexString(255 & hash[i]));
                }
            }

            sign = hexString.toString();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return sign;
    }
}

