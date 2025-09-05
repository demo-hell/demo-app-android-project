package br.com.mobicare.cielo.transparentLogin.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse

interface TransparentLoginRepository {
    suspend fun login(request: LoginRequest, ignoreSessionExpired: String, akamaiSensorData: String?): CieloDataResult<LoginResponse>
}