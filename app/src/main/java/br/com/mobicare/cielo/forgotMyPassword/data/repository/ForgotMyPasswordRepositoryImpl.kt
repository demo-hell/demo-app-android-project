package br.com.mobicare.cielo.forgotMyPassword.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.forgotMyPassword.data.dataSource.ForgotMyPasswordRemoteDataSource
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.domain.repository.ForgotMyPasswordRepository

class ForgotMyPasswordRepositoryImpl(
    private val forgotMyPasswordRemoteDataSource: ForgotMyPasswordRemoteDataSource
) : ForgotMyPasswordRepository {

    override suspend fun postForgotMyPasswordRecoveryPassword(
        params: ForgotMyPasswordRecoveryPasswordRequest,
        akamaiSensorData: String?
    ): CieloDataResult<ForgotMyPassword> {
        return forgotMyPasswordRemoteDataSource.postRecoveryPassword(
            params,
            akamaiSensorData
        )
    }

}