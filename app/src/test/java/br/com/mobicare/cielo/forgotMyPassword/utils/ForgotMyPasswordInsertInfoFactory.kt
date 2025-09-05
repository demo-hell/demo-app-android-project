package br.com.mobicare.cielo.forgotMyPassword.utils

import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword

object ForgotMyPasswordInsertInfoFactory {

    fun makeForgotMyPassword() = ForgotMyPassword(
        tokenExpirationInMinutes = 5,
        email = "a@a.com.br",
        nextStep = "nextStep",
        faceIdPartner = "faceid"
    )

    fun makeNotBootingError() = CieloAPIException.notBootingError(
        response = null,
        500,
        ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
        NewErrorMessage().apply { flagErrorCode = ERROR_NOT_BOOTING }
    )

}