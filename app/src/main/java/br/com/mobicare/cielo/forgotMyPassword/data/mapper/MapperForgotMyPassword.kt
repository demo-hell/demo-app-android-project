package br.com.mobicare.cielo.forgotMyPassword.data.mapper

import br.com.mobicare.cielo.forgotMyPassword.data.model.response.ForgotMyPasswordRecoveryPasswordResponse
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword

object MapperForgotMyPassword {
    fun mapToForgotMyPassword(forgotMyPasswordRecoveryPasswordResponse: ForgotMyPasswordRecoveryPasswordResponse?): ForgotMyPassword? {
        forgotMyPasswordRecoveryPasswordResponse?.let { response ->
            return ForgotMyPassword(
                tokenExpirationInMinutes = response.tokenExpirationInMinutes,
                email = response.email,
                nextStep = response.nextStep,
                faceIdPartner = response.faceIdPartner
            )
        }
        return null
    }
}