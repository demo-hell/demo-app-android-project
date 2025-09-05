package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage


const val INVALID_EC_NUMBER = "Nº do estabelecimento inválido"
const val INVALID_EC_MESSAGE = "Verifique o número digitado e tente novamente"

fun RetrofitException.convertToErrorMessage(
    customTitle: String = "Error",
    customMessage: String? = null
):
        ErrorMessage = ErrorMessage().apply {

    message = customMessage ?: this@convertToErrorMessage.message.toString()
    statusText = this@convertToErrorMessage.response?.raw()?.message()
    httpStatus = this@convertToErrorMessage.response?.code() ?: -1
    brokenServiceUrl = this@convertToErrorMessage.url.toString()
    this.title = customTitle
    setType(this@convertToErrorMessage.kind)

}

fun ErrorMessage.toNewErrorMessage(): NewErrorMessage {
    return NewErrorMessage(
        title = this.title,
        message = this.message,
        httpCode = this.httpStatus
    )
}