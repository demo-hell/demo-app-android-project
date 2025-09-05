package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import retrofit2.Response
import java.lang.RuntimeException

class CieloAPIException internal constructor(
    message: String? = null,
    val httpStatusCode: Int? = HTTP_STATUS_500,
    val actionErrorType: ActionErrorTypeEnum,
    val response: Response<*>? = null,
    val newErrorMessage: NewErrorMessage = NewErrorMessage()
) : RuntimeException(message) {

    companion object {

        fun httpError(
            response: Response<*>?,
            httpStatusCode: Int?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            val message = response?.code().toString() + " " + response?.message()
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun unexpectedError(
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            return CieloAPIException(
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun networkError(
            message: String,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR
            return CieloAPIException(
                message = message,
                actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR,
                newErrorMessage = newErrorMessage
            )
        }

        fun invalidToken(
            response: Response<*>?,
            httpStatusCode: Int?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            val message = response?.code().toString() + " " + response?.message()
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun mfaTokenError(
            response: Response<*>?,
            httpStatusCode: Int?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            val message = response?.code().toString() + " " + response?.message()
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun notBootingError(
            response: Response<*>?,
            httpStatusCode: Int?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            val message = response?.code().toString() + " " + response?.message()
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun withoutAccessUpdateInfo(
            response: Response<*>?,
            httpStatusCode: Int?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            val message = response?.code().toString() + " " + response?.message()
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun withoutAccess(
            response: Response<*>?,
            httpStatusCode: Int?,
            message: String?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }

        fun unauthorizedError(
            response: Response<*>?,
            httpStatusCode: Int?,
            message: String?,
            actionErrorType: ActionErrorTypeEnum,
            newErrorMessage: NewErrorMessage = NewErrorMessage()
        ): CieloAPIException {
            newErrorMessage.actionErrorType = actionErrorType
            return CieloAPIException(
                message = message,
                response = response,
                httpStatusCode = httpStatusCode,
                actionErrorType = actionErrorType,
                newErrorMessage = newErrorMessage
            )
        }
    }
}