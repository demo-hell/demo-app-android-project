package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.JSON_PARSE_SYNTAX_ERROR
import br.com.mobicare.cielo.commons.constants.Text.FORBIDDEN
import br.com.mobicare.cielo.commons.constants.Text.NOT_VALIDATED
import br.com.mobicare.cielo.commons.constants.Text.ONBOARDING_REQUIRED
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_400
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_401
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_403
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_404
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_417
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_502
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_599
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NOT_ELIGIBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ApiResponseError
import br.com.mobicare.cielo.commons.enums.MfaStatusEnums
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

open class SafeApiCaller(private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = backgroundDispatcher,
        apiCall: suspend () -> T
    ): CieloDataResult<T> {
        return withContext(dispatcher) {
            callApi(apiCall)
        }
    }

    private suspend fun <T> callApi(apiCall: suspend () -> T): CieloDataResult<T> {
        return try {
            val result = apiCall.invoke()

            if (result is Response<*> && !result.isSuccessful) {
                val httpError = getAPIErrorAndCreateAPIException(result)
                CieloDataResult.APIError(httpError)
            } else {
                if (result is Response<*> && result.body().toString().isNullOrEmpty()) {
                    CieloDataResult.Empty(result.code())
                } else {
                    CieloDataResult.Success(result)
                }
            }

        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            val result = onError(exception)
            CieloDataResult.APIError(result)
        }
    }

    private fun onError(exception: Exception): CieloAPIException {
        return when (exception) {
            is IOException -> {
                CieloAPIException.networkError(exception.message ?: EMPTY)
            }
            is HttpException -> {
                getAPIErrorAndCreateAPIException(exception.response())
            }
            is JsonSyntaxException -> {
                generateJsonParserError(exception)
            }
            else -> {
                CieloAPIException.unexpectedError(
                    actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR
                )
            }
        }
    }

    private fun getAPIErrorAndCreateAPIException(response: Response<*>?): CieloAPIException {
        val newErrorMessage = response.convertToError()
        return when (response?.code()) {
            HTTP_STATUS_400 -> {
                if (checkIsMfaError(newErrorMessage.flagErrorCode))
                    CieloAPIException.mfaTokenError(
                        response = response,
                        httpStatusCode = response.code(),
                        actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
                        newErrorMessage = newErrorMessage
                    )
                else
                    generateGenericHttpError(response, newErrorMessage)
            }
            HTTP_STATUS_401 -> {
                unauthorizedErrorHandler(newErrorMessage, response)
            }
            HTTP_STATUS_403 -> {
                forbiddenErrorHandler(response, newErrorMessage)
            }
            HTTP_STATUS_404,
            HTTP_STATUS_502 -> {
                CieloAPIException.httpError(
                    response = response,
                    httpStatusCode = response.code(),
                    actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                    newErrorMessage = newErrorMessage
                )
            }
            HTTP_STATUS_417 -> {
                if (newErrorMessage.flagErrorCode == ERROR_NOT_BOOTING)
                    CieloAPIException.notBootingError(
                        response = response,
                        httpStatusCode = response.code(),
                        actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
                        newErrorMessage = newErrorMessage
                    )
                else
                    generateGenericHttpError(response, newErrorMessage)
            }
            in HTTP_STATUS_500..HTTP_STATUS_599 -> {
                CieloAPIException.networkError(
                    message = response?.message() ?: EMPTY,
                    newErrorMessage = newErrorMessage
                )
            }
            else -> {
                generateGenericHttpError(response, newErrorMessage)
            }
        }
    }

    private fun unauthorizedErrorHandler(
        newErrorMessage: NewErrorMessage,
        response: Response<*>?
    ) = if (checkEligibleError(newErrorMessage.flagErrorCode)) {
        CieloAPIException.unauthorizedError(
            response = response,
            httpStatusCode = response?.code(),
            actionErrorType = ActionErrorTypeEnum.ELIGIBLE_ERROR,
            newErrorMessage = newErrorMessage,
            message = newErrorMessage.message
        )
    } else {
        CieloAPIException.invalidToken(
            response = response,
            httpStatusCode = response?.code(),
            actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
            newErrorMessage = newErrorMessage
        )
    }

    private fun checkIsMfaError(errorCode: String?): Boolean {
        return when (errorCode) {
            MfaStatusEnums.OTP_REQUIRED.mfaStatus,
            MfaStatusEnums.OTP_ENROLLMENT_REQUIRED.mfaStatus,
            MfaStatusEnums.OTP_ENROLLMENT_EXPIRED.mfaStatus,
            MfaStatusEnums.OTP_ENROLLMENT_PENDING.mfaStatus,
            MfaStatusEnums.OTP_ENROLLMENT_EXPIRED.mfaStatus,
            MfaStatusEnums.OTP_TEMPORARILY_BLOCKED.mfaStatus,
            MfaStatusEnums.NOT_ELIGIBLE.mfaStatus -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun checkEligibleError(errorCode: String?): Boolean {
        return errorCode == NOT_ELIGIBLE
    }

    private fun generateGenericHttpError(
        response: Response<*>?,
        newErrorMessage: NewErrorMessage = NewErrorMessage()
    ): CieloAPIException {
        return CieloAPIException.httpError(
            response = response,
            httpStatusCode = response?.code(),
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = newErrorMessage
        )
    }

    private fun forbiddenErrorHandler(
        response: Response<*>?,
        newErrorMessage: NewErrorMessage = NewErrorMessage()
    ): CieloAPIException {
        val apiResponseError = Gson().fromJson(response?.body().toString(), ApiResponseError::class.java)
        val errorCode = apiResponseError?.errorCode ?: response?.message()
        return when (errorCode?.uppercase()) {
            ONBOARDING_REQUIRED, NOT_VALIDATED -> {
                CieloAPIException.withoutAccessUpdateInfo(
                    response = response,
                    httpStatusCode = response?.code(),
                    actionErrorType = ActionErrorTypeEnum.WITHOUT_ACCESS_UPDATE_INFO,
                    newErrorMessage = newErrorMessage
                )

            }
            FORBIDDEN -> {
                CieloAPIException.withoutAccess(
                    response = response,
                    httpStatusCode = response?.code(),
                    message = apiResponseError?.errorMessage ?: EMPTY,
                    actionErrorType = ActionErrorTypeEnum.WITHOUT_ACCESS_CHECK_MAIN_ROLE,
                    newErrorMessage = newErrorMessage
                )
            }
            else -> {
                generateGenericHttpError(response, newErrorMessage)
            }
        }
    }

    private fun generateJsonParserError(exception: JsonSyntaxException): CieloAPIException{
        return CieloAPIException.unexpectedError(
            actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR,
            newErrorMessage =  NewErrorMessage(
                title = JSON_PARSE_SYNTAX_ERROR,
                message = exception.message.toString()
            )
        )
    }

}