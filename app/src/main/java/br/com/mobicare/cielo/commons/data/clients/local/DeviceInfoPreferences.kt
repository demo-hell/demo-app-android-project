package br.com.mobicare.cielo.commons.data.clients.local

import br.com.mobicare.cielo.mfa.model.deviceDna.DeviceDna
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk

object DeviceInfoPreferences {


    const val DEVICE_INFO_KEY = "br.com.cielo.security.deviceDna"

    fun saveDeviceDna(deviceDnaJson: String) {
        Hawk.put(DEVICE_INFO_KEY, deviceDnaJson)
    }

    fun getDeviceDna(): DeviceDna {
        return GsonBuilder().create()
                .fromJson(Hawk.get(DEVICE_INFO_KEY, ""),
                        DeviceDna::class.java)
    }

}