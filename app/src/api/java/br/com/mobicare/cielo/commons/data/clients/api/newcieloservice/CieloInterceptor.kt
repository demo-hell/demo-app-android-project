package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import android.os.Build
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.ERROR_417
import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.REFRESH_TOKEN
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.api.ServicesBase
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.ACCESSTOKEN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.ACCESS_TOKEN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.ANDROID
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.APP_TOKEN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.APP_VERSION
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.AUTH
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.AUTHORIZATION
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CHANNEL
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CLIENT_ID
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CODIGO_CLIENTE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CONNECT_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CONTENT_TYPE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.CONTENT_TYPE_JSON
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.DEVICE_ID
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.DEVICE_MODEL
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.EC
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HEADER_KEY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_AGENT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.IGNORE_SESSION_EXPIRED
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.IS_CONVENIENCIA_USER
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.LENGHT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NOT_ELIGIBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NO_REQUIRED
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.OS
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.READ_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.REQUIRED
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.TOKEN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.USER
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.USER_AGENT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.WRITE_TIMEOUT
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.domains.entities.ApiResponseError
import br.com.mobicare.cielo.commons.enums.MfaStatusEnums
import br.com.mobicare.cielo.commons.utils.DeviceInfo
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.Utils.authorization
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.akamai.botman.CYFMonitor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class CieloInterceptor constructor(
    private val cieloMfaTokenGenerator: CieloMfaTokenGenerator,
    private val menuPreferences: MenuPreference,
    private val mfaUserInformation: MfaUserInformation,
) : Interceptor, ServicesBase() {
    private var isProcessinSessionExpired = AtomicBoolean(false)
    private var queueLockedSessionExpired = AtomicInteger(ZERO)

    override fun intercept(chain: Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder =
            originalRequest.newBuilder()
                .addHeader(DEVICE_ID, DeviceInfo.getInstance().deviceId)
                .addHeader(OS, Build.VERSION.SDK_INT.toString())
                .addHeader(APP_VERSION, BuildConfig.VERSION_NAME)
                .addHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .addHeader(CHANNEL, ANDROID)
                .addHeader(CLIENT_ID, BuildConfig.CLIENT_ID)
                .addHeader(DEVICE_MODEL, Build.MANUFACTURER + " " + Build.MODEL)

        if (originalRequest.headers().get(HEADER_KEY) == null) {
            System.getProperty(HTTP_AGENT)?.let { agent ->
                requestBuilder.addHeader(USER_AGENT, agent)
            }
        } else {
            val userAgentForAkamai =
                "${BuildConfig.APP_NAME}/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.RELEASE}; Build${Build.ID}})"
            requestBuilder.addHeader(USER_AGENT, userAgentForAkamai)
        }

        if (originalRequest.headers().get(AUTHORIZATION) == null &&
            isAuthorizationRequired(
                originalRequest,
            )
        ) {
            requestBuilder.addHeader(AUTHORIZATION, authorization())
        }

        val token: String = userPreferences.token
        if (isTokenAppCieloRequired(token, originalRequest)) {
            requestBuilder.removeHeader(TOKEN)
            requestBuilder.addHeader(TOKEN, token)
        }

        if (isAccessTokenAppCieloRequired(
                token,
                originalRequest,
            )
        ) {
            requestBuilder.removeHeader(ACCESS_TOKEN)
            requestBuilder.addHeader(ACCESS_TOKEN, token)
        }

        requestBuilder.removeHeader(AUTH)
        requestBuilder.removeHeader(ACCESSTOKEN)
        requestBuilder.removeHeader(APP_TOKEN)

        val isConvivenciaUser = userPreferences.isConvivenciaUser
        if (isConvivenciaUser) {
            requestBuilder.addHeader(IS_CONVENIENCIA_USER, isConvivenciaUser.toString())
        }

        val ec: String? = menuPreferences.getEC()
        val userName = userPreferences.userName
        ec?.let {
            if (it.isNotEmpty() && isECRequired(originalRequest)) {
                Timber.tag(EC).e(ec)
                requestBuilder.addHeader(EC, ec)
                requestBuilder.addHeader(CODIGO_CLIENTE, ec)
                requestBuilder.addHeader(USER, userName)
            }
        }

        if (userPreferences.isAuthenticated) {
            requestBuilder.addHeader(USER, userName)
        }

        requestBuilder.removeHeader(CONNECT_TIMEOUT)
        requestBuilder.removeHeader(READ_TIMEOUT)
        requestBuilder.removeHeader(WRITE_TIMEOUT)

        MockRequestFilter(
            requestBuilder = requestBuilder,
            usernameFromPreferences = userPreferences.userName,
        ).filter()

        val request = requestBuilder.build()
        val response = chain.withTimeout().proceed(request)

        getMfaServerDateTime(request, response)

        return handlerError(
            request = request,
            response = response,
            chain = chain,
            requestBuilder = requestBuilder,
            cieloMfaTokenGenerator = cieloMfaTokenGenerator,
        )
    }

    private fun handlerError(
        request: Request,
        response: Response,
        chain: Chain,
        requestBuilder: Request.Builder,
        cieloMfaTokenGenerator: CieloMfaTokenGenerator,
    ): Response {
        return when (response.code()) {
            ERROR_417 -> {
                error417Handler(response, request)
            }
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                unauthorizedErrorHandler(
                    request = request,
                    response = response,
                    chain = chain,
                    requestBuilder = requestBuilder,
                )
            }
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                badRequestErrorHandler(
                    response = response,
                    request = request,
                    chain = chain,
                    cieloMfaTokenGenerator = cieloMfaTokenGenerator,
                )
            }
            else -> {
                return response
            }
        }
    }

    private fun unauthorizedErrorHandler(
        response: Response,
        request: Request,
        chain: Chain,
        requestBuilder: Request.Builder,
    ): Response {
        val originalRequest = chain.request()
        val lock = ReentrantLock()
        val responseBody = response.peekBody(Long.MAX_VALUE).string()
        try {
            queueLockedSessionExpired.incrementAndGet()
            lock.lock()

            val token = userPreferences.token
            if (token.isNotEmpty() and ignoreSessionExpired(token, request).not()) {
                if (!isProcessinSessionExpired.get()) {
                    isProcessinSessionExpired.set(true)
                    if (!responseBody.contains(NOT_ELIGIBLE) && !refreshToken()) {
                        return response
                    }
                }

                if (SessionExpiredHandler.sessionCalled.not()) {
                    val newRequest = request.newBuilder()
                    if (request.headers().get(TOKEN).isNullOrEmpty().not()) {
                        newRequest.removeHeader(TOKEN)
                        newRequest.addHeader(TOKEN, userPreferences.token)
                    }

                    if (request.headers().get(ACCESS_TOKEN).isNullOrEmpty().not()) {
                        newRequest.removeHeader(ACCESS_TOKEN)
                        newRequest.addHeader(ACCESS_TOKEN, userPreferences.token)
                    }

                    response.close()

                    return chain.proceed(newRequest.build())
                }
            }

            if (ignoreSessionExpired(token, originalRequest)) {
                requestBuilder.removeHeader(IGNORE_SESSION_EXPIRED)
            }
        } finally {
            val queueSize = queueLockedSessionExpired.decrementAndGet()
            if (queueSize == ZERO) {
                isProcessinSessionExpired.set(false)
            }
            lock.unlock()
        }
        return response
    }

    private fun badRequestErrorHandler(
        response: Response,
        request: Request,
        chain: Chain,
        cieloMfaTokenGenerator: CieloMfaTokenGenerator,
    ): Response {
        var responseBody: ResponseBody? = null
        var body: String? = null

        try {
            responseBody = response.body()
            responseBody?.let {
                body = it.string()
                val apiResponse = Gson().fromJson(body, ApiResponseError::class.java)
                apiResponse.errorCode?.let { errorCode ->
                    when (errorCode) {
                        MfaStatusEnums.OTP_REQUIRED.mfaStatus -> {
                            val currentMfaUser = mfaUserInformation.getCurrentMfaUser()

                            if (currentMfaUser != null) {
                                val newRequest = request.newBuilder()
                                val seed = currentMfaUser.mfaSeed

                                val newOtpToken = cieloMfaTokenGenerator.getOtpCode(seed) ?: EMPTY

                                newRequest.removeHeader(CieloAPIServices.OTP_CODE)
                                newRequest.addHeader(CieloAPIServices.OTP_CODE, newOtpToken)
                                return chain.proceed(newRequest.build())
                            }
                        }
                        else -> {
                            return returnNewResponseBody(
                                response,
                                responseBody,
                                body,
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (e !is JsonSyntaxException) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            return returnNewResponseBody(response, responseBody, body)
        }

        return response
    }

    private fun error417Handler(
        response: Response,
        request: Request,
    ): Response {
        if (response.code() == ERROR_417 && errorResponse(response).errorCode == ERROR_NOT_BOOTING) {
            if (request.headers().get(REFRESH_TOKEN) != null) {
                return returnNewResponseBody(response, response.body(), ERROR_NOT_BOOTING)
            }
        }
        return response
    }

    private fun isAuthorizationRequired(request: Request): Boolean {
        val value = request.headers().get(AUTH)
        return ((value == null) || (value == REQUIRED))
    }

    private fun isTokenAppCieloRequired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get(AUTH)
        return (value == null || value == REQUIRED) && token.isNotEmpty()
    }

    private fun isAccessTokenAppCieloRequired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get(ACCESSTOKEN)
        return (value == null || value == REQUIRED) && token.isNotEmpty()
    }

    private fun ignoreSessionExpired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get(IGNORE_SESSION_EXPIRED)
        return value != null && value == REQUIRED && token.isNotEmpty()
    }

    private fun isECRequired(request: Request): Boolean {
        val value = request.headers().get(EC)
        return ((value == null) || (value != NO_REQUIRED))
    }

    private fun returnNewResponseBody(
        response: Response,
        responseBody: ResponseBody?,
        body: String?,
    ): Response {
        return response.newBuilder()
            .body(ResponseBody.create(responseBody?.contentType(), body.orEmpty())).build()
    }

    private fun errorResponse(response: Response): ApiResponseError {
        val responseBody = response.peekBody(LENGHT.toLong())

        return if (responseBody.contentLength().toInt() > ZERO) {
            Gson().fromJson(
                returnNewResponseBody(
                    response,
                    responseBody,
                    responseBody.string(),
                ).peekBody(LENGHT.toLong()).string(),
                ApiResponseError::class.java,
            )
        } else {
            ApiResponseError()
        }
    }

    private fun refreshToken(): Boolean {
        val api = createCieloService(CieloAPI::class.java, BuildConfig.HOST_API, this)

        val refreshCall: Call<LoginResponse> =
            api.callRefreshToken(
                userPreferences.token,
                userPreferences.refreshToken,
                CYFMonitor.getSensorData(),
            )

        val refreshResponse: retrofit2.Response<LoginResponse> = refreshCall.execute()
        if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
            val accessToken = refreshResponse.body()?.accessToken
            val refreshToken = refreshResponse.body()?.refreshToken

            userPreferences.saveToken(accessToken)
            userPreferences.saveRefreshToken(refreshToken)
            return true
        }
        userPreferences.saveToken(EMPTY)
        return false
    }
}
