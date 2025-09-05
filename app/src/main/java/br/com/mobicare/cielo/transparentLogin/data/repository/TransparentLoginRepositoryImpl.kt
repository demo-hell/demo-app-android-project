package br.com.mobicare.cielo.transparentLogin.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.transparentLogin.data.TransparentLoginRemoteDataSource
import br.com.mobicare.cielo.transparentLogin.domain.repository.TransparentLoginRepository

class TransparentLoginRepositoryImpl(
    private val dataSource: TransparentLoginRemoteDataSource
): TransparentLoginRepository {
    override suspend fun login(request: LoginRequest, ignoreSessionExpired: String, akamaiSensorData: String?): CieloDataResult<LoginResponse> {
        return dataSource.login(request, ignoreSessionExpired, akamaiSensorData)
    }
}