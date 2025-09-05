package br.com.mobicare.cielo.newLogin

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceRequest
import com.akamai.botman.CYFMonitor

class LoginDataSource(private val api: CieloAPI) {

    fun login(request: LoginRequest, requiredSessionExpired: String, akamaiSensorData: String?) =
        api.login(request, requiredSessionExpired, akamaiSensorData)

    fun postRegisterDevice(faceIdToken: String, body: PostRegisterDeviceRequest) =
        api.postRegisterDevice(faceIdToken, body)

    fun refreshToken(accessToken: String?, refreshToken: String?) =
        api.refreshToken(accessToken, refreshToken, CYFMonitor.getSensorData())
}