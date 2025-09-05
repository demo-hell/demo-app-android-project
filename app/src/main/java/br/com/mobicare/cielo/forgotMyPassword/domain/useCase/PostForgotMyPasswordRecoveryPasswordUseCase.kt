package br.com.mobicare.cielo.forgotMyPassword.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.domain.repository.ForgotMyPasswordRepository

class PostForgotMyPasswordRecoveryPasswordUseCase(
    private val repository: ForgotMyPasswordRepository
) {
    suspend operator fun invoke(
        params: ForgotMyPasswordRecoveryPasswordRequest,
        akamaiSensorData: String?
    ) = repository.postForgotMyPasswordRecoveryPassword(
            params,
            akamaiSensorData
        )
}