package br.com.mobicare.cielo.forgotMyPassword.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword

interface ForgotMyPasswordRepository {

    suspend fun postForgotMyPasswordRecoveryPassword(
        params: ForgotMyPasswordRecoveryPasswordRequest,
        akamaiSensorData: String?
    ): CieloDataResult<ForgotMyPassword>

}