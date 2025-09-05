package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ERROR
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NONE
import br.com.mobicare.cielo.commons.domains.entities.ErrorListServer

const val DEFAULT_ERROR_MESSAGE = "Infelizmente ocorreu algum erro. Por favor, tente novamente."
data class NewErrorMessage(
    var title: String = ERROR,
    var message: String = DEFAULT_ERROR_MESSAGE,
    var httpCode: Int = HTTP_STATUS_500,
    var brokenServiceUrl: String? = NONE,
    var listErrorServer: List<ErrorListServer> = listOf(),
    var flagErrorCode: String = EMPTY,
    var actionErrorType: ActionErrorTypeEnum = ActionErrorTypeEnum.HTTP_ERROR,
    var mfaErrorCode: String = EMPTY
)

