
package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.BRACKET_BRACES
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.COMMA_SPACE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CONNECT_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.READ_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.WRITE_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.domains.entities.ApiResponseError
import br.com.mobicare.cielo.commons.domains.entities.ErrorListServer
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.MASTER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import br.com.mobicare.cielo.main.presentation.ui.activities.ERROR_CODE_OTP
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

fun HttpLoggingInterceptor.getSimpleLogging(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        .setLevel(HttpLoggingInterceptor.Level.BODY)
}

fun Interceptor.Chain.withTimeout(): Interceptor.Chain {

    val request: Request = this.request()

    val connectTimeout =
        request.header(CONNECT_TIMEOUT)?.toIntOrNull() ?: connectTimeoutMillis()
    val readTimeout =
        request.header(READ_TIMEOUT)?.toIntOrNull() ?: readTimeoutMillis()
    val writeTimeout =
        request.header(WRITE_TIMEOUT)?.toIntOrNull() ?: writeTimeoutMillis()

    return this
        .withConnectTimeout(
            connectTimeout,
            TimeUnit.MILLISECONDS
        )
        .withReadTimeout(
            readTimeout,
            TimeUnit.MILLISECONDS
        )
        .withWriteTimeout(
            writeTimeout,
            TimeUnit.MILLISECONDS
        )
}

fun Response<*>?.convertToError(): NewErrorMessage {
    var newErrorMsg = NewErrorMessage()
    var serverError = ErrorListServer(
        errorCode = EMPTY,
        errorMessage = EMPTY,
        value = EMPTY,
        type = EMPTY
    )

    try {
        val json = getResponseErrorString(this)
        if (json.startsWith(BRACKET_BRACES)) {
            newErrorMsg.listErrorServer = getListErrors(json)
        } else {
            val localError = getErrorMessage(json)
            localError?.let {
                serverError = it
            }
        }

        newErrorMsg.brokenServiceUrl = this?.raw()?.request()?.url().toString()
        newErrorMsg.httpCode = this?.code() ?: HTTP_STATUS_500

        newErrorMsg.flagErrorCode = serverError.errorCode

        val responseBody = this?.body()
        responseBody?.let { it ->
            val body = it.toString()
            val apiResponse = Gson().fromJson(body, ApiResponseError::class.java)
            apiResponse?.let { apiResponse ->
                newErrorMsg.mfaErrorCode = apiResponse.errorCode.toString()
            }
        }

        if (newErrorMsg.listErrorServer.isNotEmpty()) {
            newErrorMsg.message = listErrorToString(newErrorMsg.listErrorServer)
        } else if (serverError.errorMessage.isNotEmpty()) {
            newErrorMsg.message = serverError.errorMessage
        }

    } catch (e: Exception) {
        newErrorMsg = createGenericNewErrorMessage(this)
    } finally {
        return newErrorMsg
    }
}

private fun getListErrors(json: String): List<ErrorListServer> {
    val listType = object : TypeToken<List<ErrorListServer>>() {}.type
    return Gson().fromJson(json, listType)
}

private fun getResponseErrorString(response: Response<*>?): String {
    response?.errorBody()?.let { responseBody ->
        val result = StringBuilder()
        val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
        reader.forEachLine {
            result.append(it)
        }
        return result.toString()
    }
    return EMPTY
}

private fun getErrorMessage(json: String): ErrorListServer? {
    return if (json.isNotBlank()) {
        Gson().fromJson(json, ErrorListServer::class.java)
    } else {
        null
    }
}

private fun listErrorToString(listErrorServer: List<ErrorListServer>): String {
    val resultError = StringBuffer()
    listErrorServer.forEach {
        if (resultError.isNotEmpty())
            resultError.append(COMMA_SPACE)
        resultError.append(it.errorMessage)
    }
    return resultError.toString()
}

private fun createGenericNewErrorMessage(response: Response<*>?): NewErrorMessage {
    val newErrorMessage = NewErrorMessage()

    response.let {
        newErrorMessage.httpCode = response?.code() ?: HTTP_STATUS_500
        newErrorMessage.brokenServiceUrl =
            response?.raw()?.request()?.url().toString()
        newErrorMessage.message = response?.raw()?.message().toString()

    }
    return newErrorMessage
}

fun mfaErrorHandler(errorMessage: NewErrorMessage, context: Context) {
    if (errorMessage.mfaErrorCode.isNotEmpty()) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(BaseLoggedActivity.MFA_TOKEN_ERROR_ACTION).apply {
                putExtra(ERROR_CODE_OTP, errorMessage.mfaErrorCode)
            })
    } else if (errorMessage.flagErrorCode.isNotEmpty()) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(BaseLoggedActivity.MFA_TOKEN_ERROR_ACTION).apply {
                putExtra(ERROR_CODE_OTP, errorMessage.flagErrorCode)
            })
    }
}

private fun withoutAccessUpdateInfoHandler(context: Context) {
    RoleWithoutAccessHandler.broadcastRoleWithoutAccessUpdateInfo(
        context
    )
}

private fun withoutAccessCheckMainRoleHandler(
    newErrorMessage: NewErrorMessage,
    context: Context,
    userObj: UserObj
) {
    when (userObj.mainRole) {
        UserObj.ADMIN -> {
            RoleWithoutAccessHandler.broadcastRoleWithoutAccessAdmin(
                context, newErrorMessage.message
            )
        }
        else -> RoleWithoutAccessHandler.broadcastRoleWithoutAccess(
            context
        )
    }
}

suspend fun newErrorHandler(
    context: Context,
    getUserObjUseCase: GetUserObjUseCase,
    newErrorMessage: NewErrorMessage,
    onHideLoading: () -> Unit = {},
    onErrorAction: () -> Unit
) {
    getUserObjUseCase()
        .onSuccess { user ->
            when (newErrorMessage.actionErrorType) {
                ActionErrorTypeEnum.HTTP_ERROR,
                ActionErrorTypeEnum.NETWORK_ERROR -> {
                    onErrorAction.invoke()
                }
                ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION -> {
                    onHideLoading.invoke()
                    mfaErrorHandler(newErrorMessage, context)
                }
                ActionErrorTypeEnum.WITHOUT_ACCESS_UPDATE_INFO -> {
                    onHideLoading.invoke()
                    withoutAccessUpdateInfoHandler(context)
                }
                ActionErrorTypeEnum.WITHOUT_ACCESS_CHECK_MAIN_ROLE -> {
                    if (user.mainRole != MASTER && user.mainRole != TECHNICAL) {
                        onHideLoading.invoke()
                        withoutAccessCheckMainRoleHandler(newErrorMessage, context, user)
                    } else {
                        onErrorAction.invoke()
                    }
                }
                ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR -> {
                    onHideLoading.invoke()
                    context.logout()
                }
                ActionErrorTypeEnum.ELIGIBLE_ERROR -> {
                    onErrorAction.invoke()
                }
            }
        }.onError {
            onHideLoading.invoke()
            context.logout()
        }.onEmpty {
            onHideLoading.invoke()
            context.logout()
        }
}

suspend fun newErrorHandler(
    getUserObjUseCase: GetUserObjUseCase,
    newErrorMessage: NewErrorMessage,
    onHideLoading: () -> Unit = {},
    onErrorAction: () -> Unit
) {
    newErrorHandler(
        context,
        getUserObjUseCase,
        newErrorMessage,
        onHideLoading,
        onErrorAction
    )

}


private fun Context.logout() {
    SessionExpiredHandler.userSessionExpires(this, true)
}