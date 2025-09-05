package br.com.mobicare.cielo.commons.data.clients.api

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupliesResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.*
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.ImpersonateRequest
import br.com.mobicare.cielo.coil.domain.MerchantAddressResponse
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domain.MerchantSuppliesResponde
import br.com.mobicare.cielo.coil.domain.MerchantSupplyChosen
import br.com.mobicare.cielo.commons.constants.ERROR_417
import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.MainConstants.BUILD_TYPE_RELEASE
import br.com.mobicare.cielo.commons.constants.MainConstants.FLAVOR_STORE
import br.com.mobicare.cielo.commons.constants.REFRESH_TOKEN
import br.com.mobicare.cielo.commons.constants.Text.FORBIDDEN
import br.com.mobicare.cielo.commons.constants.Text.NOT_VALIDATED
import br.com.mobicare.cielo.commons.constants.Text.ONBOARDING_REQUIRED
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.MockRequestFilter
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.utils.RxErrorHandlingCallAdapterFactory
import br.com.mobicare.cielo.commons.domains.entities.ApiResponseError
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.MessagePhoto
import br.com.mobicare.cielo.commons.enums.MfaStatusEnums
import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity.Companion.MFA_TOKEN_ERROR_ACTION
import br.com.mobicare.cielo.commons.utils.DeviceInfo
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPasswordResponse
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import br.com.mobicare.cielo.login.domain.*
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.CUSTOM
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.MASTER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse
import br.com.mobicare.cielo.main.domain.*
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.ui.activities.ERROR_CODE_OTP
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest
import br.com.mobicare.cielo.merchant.domain.entity.MerchantPermissionsEligible
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.*
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.PostingsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaResendRequest
import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import br.com.mobicare.cielo.mfa.api.MfaEnrollmentRequest
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.migration.domain.MigrationRequest
import br.com.mobicare.cielo.minhasVendas.domain.*
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.RequestCancelApi
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseCancelVenda
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.notification.domain.NotificationCountResponse
import br.com.mobicare.cielo.notification.domain.NotificationResponse
import br.com.mobicare.cielo.orders.domain.OrderReplacementRequest
import br.com.mobicare.cielo.orders.domain.OrderRequest
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyRequest
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLinkResponse
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersResponse
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.ResponseEligibilityPix
import br.com.mobicare.cielo.pix.domain.ResponsePixDataQuery
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domain.HelpCenterResponse
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratacaoResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest
import br.com.mobicare.cielo.recebaMais.domains.entities.ResumoResponse
import br.com.mobicare.cielo.research.domains.entities.ResearchRating
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
import br.com.mobicare.cielo.selfRegistration.domains.AccountRegistrationPayLoadRequest
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationResponse
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosOverviewResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import com.akamai.botman.CYFMonitor
import com.datadog.android.DatadogEventListener
import com.datadog.android.DatadogInterceptor
import com.datadog.android.tracing.TracingInterceptor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class CieloAPIServices private constructor(
    val context: Context,
) : ServicesBase() {
    private val retrofit: Retrofit
    private val api: CieloAPI
    private val TIMEOUT: Long = 65
    private var cieloApiHolder: CieloApiHolder? = null
    private var cieloMfaTokenGenerator: CieloMfaTokenGenerator? = null
    private var mfaUserInformation: MfaUserInformation? = null
    private val LENGHT = 248

    companion object {
        private lateinit var baseUrl: String
        private val lock = ReentrantLock()
        private var isProcessinSessionExpired = AtomicBoolean(false)
        private var queueLockedSessionExpired = AtomicInteger(0)

        const val CONNECT_TIMEOUT = "connect-timeout"
        const val READ_TIMEOUT = "read-timeout"
        const val WRITE_TIMEOUT = "write-timeout"
        const val OTP_CODE = "otpCode"

        fun getInstance(
            context: Context,
            url: String = BuildConfig.SERVER_URL,
        ): CieloAPIServices {
            baseUrl =
                when (url) {
                    BuildConfig.SERVER_URL -> BuildConfig.SERVER_URL
                    BuildConfig.HOST_API -> BuildConfig.HOST_API
                    BuildConfig.GOOGLE_MAPS_SERVER_URL -> BuildConfig.GOOGLE_MAPS_SERVER_URL
                    else -> url
                }
            return CieloAPIServices(context)
        }

        fun getCieloBackInstance(context: Context): CieloAPIServices {
            baseUrl = BuildConfig.HOST_API
            return CieloAPIServices(context)
        }

        fun getErrorMessage(error: Throwable): ErrorMessage {
            return ErrorMessage().apply {
                message = "Ocorreu um erro genérico."
            }
        }
    }

    class CieloApiHolder(var api: CieloAPI? = null)

    /**
     * Adicionando as configurações do HEADER para acesso ao MIP

     * @return
     */
    private val client: OkHttpClient
        get() {
            this.cieloApiHolder = CieloApiHolder()
            val httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.addInterceptor(DatadogInterceptor(listOf(BuildConfig.HOST_API)))
            httpClient.addNetworkInterceptor(TracingInterceptor(listOf(BuildConfig.HOST_API)))
            httpClient.eventListenerFactory(DatadogEventListener.Factory())
            httpClient.addInterceptor(
                object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                        return onProcessIntercept(chain)
                    }

                    private fun onProcessIntercept(chain: Interceptor.Chain): okhttp3.Response {
                        val original = chain.request()
                        val requestBuilder =
                            original.newBuilder()
                                .addHeader("deviceId", DeviceInfo.getInstance().deviceId)
                                .addHeader("os", Build.VERSION.SDK_INT.toString())
                                .addHeader("appVersion", BuildConfig.VERSION_NAME)
                                .addHeader("Content-Type", "application/json")
                                .addHeader("channel", "ANDROID")
                                .addHeader("client_id", BuildConfig.CLIENT_ID)
                                .addHeader("deviceModel", Build.MANUFACTURER + " " + Build.MODEL)

                        if (original.headers().get("X-acf-sensor-data") == null) {
                            System.getProperty("http.agent")?.let { agent ->
                                requestBuilder.addHeader("User-Agent", agent)
                            }
                        } else {
                            val userAgentForAkamai =
                                context.getString(
                                    R.string.user_agent_for_akamai,
                                    BuildConfig.APP_NAME,
                                    BuildConfig.VERSION_NAME,
                                    Build.VERSION.RELEASE,
                                    Build.ID,
                                )
                            requestBuilder.addHeader("User-Agent", userAgentForAkamai)
                        }

                        if (original.headers().get("Authorization") == null &&
                            isAuthorizationRequired(
                                original,
                            )
                        ) {
                            requestBuilder.addHeader("Authorization", Utils.authorization())
                        }

                        val token = UserPreferences.getInstance().token

                        if (isTokenAppCieloRequired(token, original)) {
                            requestBuilder.removeHeader("token")
                            requestBuilder.addHeader("token", token)
                        }
                        if (isAccessTokenAppCieloRequired(token, original)) {
                            requestBuilder.removeHeader("access_token")
                            requestBuilder.addHeader("access_token", token)
                        }

                        requestBuilder.removeHeader("auth")
                        requestBuilder.removeHeader("accessToken")
                        requestBuilder.removeHeader("appToken")

                        requestBuilder.addHeader(
                            "isConvivenciaUser",
                            UserPreferences.getInstance().isConvivenciaUser.toString(),
                        )

                        val ec = MenuPreference.instance.getEC()
                        if (ec != null && ec.isNotEmpty() && isECRequired(original)) {
                            Timber.tag("ec").e(ec)

                            requestBuilder.addHeader("ec", ec)
                            requestBuilder.addHeader("codigoCliente", ec)
                            requestBuilder.addHeader("user", UserPreferences.getInstance().userName)
                        }
                        if (UserPreferences.getInstance().isAuthenticated) {
                            val user = UserPreferences.getInstance().userName
                            requestBuilder.addHeader("user", user)
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

                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            try {
                                queueLockedSessionExpired.incrementAndGet()
                                lock.lock()

                                if (token.isNotEmpty() && ignoreSessionExpired(token, request).not()) {
                                    if (!isProcessinSessionExpired.get()) {
                                        isProcessinSessionExpired.set(true)
                                        if (!refreshToken()) {
                                            SessionExpiredHandler.userSessionExpires(context, true)
                                            return response
                                        }
                                    }
                                    if (SessionExpiredHandler.sessionCalled.not()) {
                                        val newRequest = request.newBuilder()
                                        if (request.headers().get("token").isNullOrEmpty().not()) {
                                            newRequest.removeHeader("token")
                                            newRequest.addHeader(
                                                "token",
                                                UserPreferences.getInstance().token,
                                            )
                                        }

                                        if (request.headers().get("access_token").isNullOrEmpty().not()) {
                                            newRequest.removeHeader("access_token")
                                            newRequest.addHeader(
                                                "access_token",
                                                UserPreferences.getInstance().token,
                                            )
                                        }
                                        return chain.proceed(newRequest.build())
                                    }
                                }
                                if (ignoreSessionExpired(token, original)) {
                                    requestBuilder.removeHeader("ignoreSessionExpired")
                                }
                            } finally {
                                val queueSize = queueLockedSessionExpired.decrementAndGet()
                                if (queueSize == 0) {
                                    isProcessinSessionExpired.set(false)
                                }

                                lock.unlock()
                            }
                        }

                        if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                            var responseBody: ResponseBody? = null
                            var body: String? = null
                            try {
                                responseBody = response.body()
                                responseBody?.let {
                                    body = it.string()
                                    val apiResponse =
                                        Gson().fromJson(body, ApiResponseError::class.java)
                                    apiResponse.errorCode?.let { errorCode ->
                                        when (errorCode) {
                                            MfaStatusEnums.OTP_REQUIRED.mfaStatus -> {
                                                val currentMfaUser = mfaUserInformation?.getCurrentMfaUser()

                                                if (currentMfaUser != null) {
                                                    val newRequest = request.newBuilder()
                                                    val seed = currentMfaUser.mfaSeed

                                                    val newOtpToken = cieloMfaTokenGenerator?.getOtpCode(seed) ?: EMPTY

                                                    newRequest.removeHeader(OTP_CODE)
                                                    newRequest.addHeader(OTP_CODE, newOtpToken)
                                                    return chain.proceed(newRequest.build())
                                                } else {
                                                    LocalBroadcastManager.getInstance(context)
                                                        .sendBroadcast(
                                                            Intent(MFA_TOKEN_ERROR_ACTION).apply {
                                                                putExtra(
                                                                    ERROR_CODE_OTP,
                                                                    apiResponse.errorCode,
                                                                )
                                                            },
                                                        )
                                                }
                                            }
                                            MfaStatusEnums.OTP_ENROLLMENT_REQUIRED.mfaStatus,
                                            MfaStatusEnums.OTP_ENROLLMENT_EXPIRED.mfaStatus,
                                            MfaStatusEnums.OTP_ENROLLMENT_PENDING.mfaStatus,
                                            MfaStatusEnums.OTP_TEMPORARILY_BLOCKED.mfaStatus,
                                            MfaStatusEnums.NOT_ELIGIBLE.mfaStatus,
                                            -> {
                                                LocalBroadcastManager.getInstance(context)
                                                    .sendBroadcast(
                                                        Intent(MFA_TOKEN_ERROR_ACTION).apply {
                                                            putExtra(ERROR_CODE_OTP, apiResponse.errorCode)
                                                        },
                                                    )
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
                        }

                        if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                            val error = errorResponse(response)
                            val errorCode = error.errorCode ?: response.message()
                            when (errorCode.uppercase()) {
                                ONBOARDING_REQUIRED, NOT_VALIDATED ->
                                    RoleWithoutAccessHandler.broadcastRoleWithoutAccessUpdateInfo(
                                        context,
                                    )
                                FORBIDDEN -> {
                                    MenuPreference.instance.getUserObj()?.run {
                                        when (mainRole) {
                                            ADMIN -> {
                                                RoleWithoutAccessHandler.broadcastRoleWithoutAccessAdmin(
                                                    context,
                                                    error.errorMessage ?: EMPTY,
                                                )
                                            }
                                            MASTER, TECHNICAL, CUSTOM -> return response
                                            else ->
                                                RoleWithoutAccessHandler.broadcastRoleWithoutAccess(
                                                    context,
                                                )
                                        }
                                    }
                                }
                                else -> return response
                            }
                        }

                        if (response.code() == ERROR_417 && errorResponse(response).errorCode == ERROR_NOT_BOOTING) {
                            if (request.headers().get(REFRESH_TOKEN) != null) {
                                SessionExpiredHandler.userSessionExpires(context, true)
                            }
                        }
                        return response
                    }

                    private fun Interceptor.Chain.withTimeout(): Interceptor.Chain {
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
                                TimeUnit.MILLISECONDS,
                            )
                            .withReadTimeout(
                                readTimeout,
                                TimeUnit.MILLISECONDS,
                            )
                            .withWriteTimeout(
                                writeTimeout,
                                TimeUnit.MILLISECONDS,
                            )
                    }
                },
            )

            if (BuildConfig.BUILD_TYPE.equals(BUILD_TYPE_RELEASE, true).not()) {
                val logging =
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS)
                        .setLevel(HttpLoggingInterceptor.Level.BODY)

                httpClient.addInterceptor(logging).build()
            }

            val isToggle =
                FeatureTogglePreference.instance.isActivate(FeatureTogglePreference.CERTIFICATE_PINNING)

            if (isToggle && BuildConfig.FLAVOR == FLAVOR_STORE && BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE) {
                val certificate =
                    CertificatePinner.Builder()
                        .add(
                            BuildConfig.URL_CERTIFICATE_PRD,
                            BuildConfig.SHA_CERTIFICATE_PRD_RSA_AKAMAI,
                        )
                        .build()
                httpClient.certificatePinner(certificate)
            }

            return httpClient.build()
        }

    private fun errorResponse(response: okhttp3.Response): ApiResponseError {
        val responseBody = response.peekBody(LENGHT.toLong())
        return Gson().fromJson(
            returnNewResponseBody(
                response,
                responseBody,
                responseBody.string(),
            ).peekBody(LENGHT.toLong()).string(),
            ApiResponseError::class.java,
        )
    }

    private fun returnNewResponseBody(
        response: okhttp3.Response,
        responseBody: ResponseBody?,
        body: String?,
    ): okhttp3.Response {
        if (responseBody != null && body != null) {
            return response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(), body)).build()
        }
        return response
    }

    private fun isTokenAppCieloRequired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get("appToken")
        return (value == null || value == "required") && token.isNotEmpty()
    }

    private fun ignoreSessionExpired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get("ignoreSessionExpired")
        return value != null && value == "required" && token.isNotEmpty()
    }

    private fun isAccessTokenAppCieloRequired(
        token: String,
        request: Request,
    ): Boolean {
        val value = request.headers().get("accessToken")
        return (value == null || value == "required") && token.isNotEmpty()
    }

    fun isAuthorizationRequired(request: Request): Boolean {
        val value = request.headers().get("auth")
        return ((value == null) || (value == "required"))
    }

    fun isECRequired(request: Request): Boolean {
        val value = request.headers().get("ec")
        return ((value == null) || (value != "no-required"))
    }

    fun refreshToken(): Boolean {
        try {
            var cieloAPI = this.api
            if (!BuildConfig.HOST_API.contains(this.retrofit.baseUrl().host())) {
                cieloAPI = getCieloBackInstance(this.context).api
            }
            cieloAPI.refreshToken(
                UserPreferences.getInstance().token,
                UserPreferences.getInstance().refreshToken,
                CYFMonitor.getSensorData(),
            ).blockingFirst()?.let {
                UserPreferences.getInstance().saveToken(it.accessToken)
                UserPreferences.getInstance().saveRefreshToken(it.refreshToken)
                return true
            }
        } catch (t: Throwable) {
            t.message?.logFirebaseCrashlytics()
        }
        UserPreferences.getInstance().saveToken("")
        return false
    }

    init {
        retrofit =
            Retrofit.Builder()
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create(context))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()

        api = retrofit.create(CieloAPI::class.java)
        this.cieloApiHolder?.api = api
        cieloMfaTokenGenerator =
            CieloMfaTokenGenerator(
                userPreferences = userPreferences,
                featureTogglePreference = FeatureTogglePreference.instance,
            )
        mfaUserInformation = MfaUserInformation(UserPreferences())
    }

    fun createAPI(clazz: Class<*>) = retrofit.create(clazz)

    /**
     * Retorna a imagem do mapa
     */
    fun getMapaURL(address: String?): Observable<MeuCadastroEndereco> = api.googleMaps(address)

    /**
     * Envia dados para alteração de senha

     * @param ec
     * *
     * @param data
     * *
     * @return
     */
    fun recoveryPassword(
        data: RecoveryPassword,
        akamaiSensorData: String?,
    ): Observable<RecoveryPasswordResponse> = api.recoveryPassword(data, akamaiSensorData)

    /**
     * Envia os dados para recuperar o usuário

     * @param doc
     * *
     * @return
     */
    fun recoveryUser(doc: String): Observable<EsqueciUsuarioObj> = api.recoveryUser(Utils.unmask(doc))

    /**
     * Envia email para o usuário

     * @param doc
     * *
     * @return
     */
    fun sendEmail(
        userId: String?,
        ec: String?,
    ): Observable<EsqueciUsuarioObj> = api.sendEmail(userId, ec)

    /**
     * Envia os dados para recuperar o usuário

     * @param doc
     * *
     * @return
     */
    fun recoveryEstableshiment(doc: String): Observable<EsqueciEstabelecimentoObj> = api.recoveryEstablishment(Utils.unmask(doc))

    /**
     * Retorna os dados da Central de Ajuda,
     * Caso o usuário esteja autenticado retorna o Gerente Comercial junto

     * @return
     */
    fun registrationData(accessToken: String): Observable<CentralAjudaObj> = api.registrationData(accessToken)

    fun unloggedRegistrationData(): Observable<CentralAjudaObj> = api.unloggedRegistrationData()

    fun getCaculationVision(
        authorization: String,
        access_token: String,
        initialDate: String,
        finalDate: String,
    ): Observable<SummaryResponse> {
        return api.getCaculationVision(
            authorization,
            access_token,
            BuildConfig.CLIENT_ID,
            initialDate,
            finalDate,
        )
    }

    fun getCaculationVisionGraph(
        authorization: String,
        access_token: String,
        client_Id: String,
        initialDate: String,
        finalDate: String,
    ): Observable<PostingsResponse> {
        return api.getCaculationVisionGraph(
            authorization,
            access_token,
            client_Id,
            initialDate,
            finalDate,
        )
    }

    fun getSummaryView(
        url: String,
        bearerToken: String,
        accessToken: String,
        initialDate: String?,
        finalDate: String?,
        cardBrands: List<Int>?,
        paymentTypes: List<Int>?,
        roNumber: String?,
        page: Int?,
        pageSize: Int?,
    ) = api.getSummaryView(
        url,
        bearerToken,
        accessToken,
        BuildConfig.CLIENT_ID,
        initialDate,
        finalDate,
        cardBrands,
        paymentTypes,
        roNumber,
        page,
        pageSize,
    )

    fun getDetailSummaryView(
        url: String,
        customId: String?,
        initialDate: String?,
        finalDate: String?,
        paymentTypeCode: List<Int>?,
        cardBrandCode: List<Int>?,
        authorizationCode: String?,
        nsu: Int?,
        operationNumber: String?,
        roNumber: String?,
        initialAmount: Double?,
        finalAmount: Double?,
        saleCode: String?,
        transactionId: String?,
        truncatedCardNumber: String?,
        terminal: String?,
        transactionTypeCode: Int?,
        merchantId: String?,
        page: Int?,
        pageSize: Int?,
    ) = api.getDetailSummaryView(
        url,
        customId,
        initialDate,
        finalDate,
        paymentTypeCode,
        cardBrandCode,
        authorizationCode,
        nsu,
        operationNumber,
        roNumber,
        initialAmount,
        finalAmount,
        saleCode,
        transactionId,
        truncatedCardNumber,
        terminal,
        transactionTypeCode,
        merchantId,
        page,
        pageSize,
    )

    /**
     * feature toggle
     */
    fun getFeatureToggle(
        system: String?,
        version: String?,
        platform: String?,
        page: Int?,
    ): Observable<FeatureToggleResponse> {
        return api.getFeatureToggle(system = system, version = version, platform = platform, page = page)
    }

    fun fetchTechnicalSupport(): Observable<List<SupportItem>> {
        return api.fetchTechnicalSupport()
    }

    fun allBanks(): Observable<BanksSet> {
        return api.allBanks()
    }

    fun getChangePassword(
        accessToken: String,
        authorization: String,
        body: BodyChangePassword,
    ): Observable<Response<Void>> {
        return api.getChangePassword(accessToken, authorization, body)
    }

    fun sendTokenFCM(tokenFCM: TokenFCM): Observable<SendDeviceTokenResponse> {
        return api.sendTokenFCM(tokenFCM)
    }

    /**
     Pesquisa Avaliação
     */
    fun getResearch(
        shouldUseNewResearchEnvironment: Boolean,
        username: String,
        merchantId: String,
    ): Observable<ResearchResponse?> {
        return if (shouldUseNewResearchEnvironment) {
            api.getResearch(username, merchantId)
        } else {
            api.getResearchOld(username, merchantId)
        }
    }

    fun saveResearch(
        researchRating: ResearchRating,
        username: String,
        merchantId: String,
        shouldUseNewResearchEnvironment: Boolean,
    ): Completable {
        return if (shouldUseNewResearchEnvironment) {
            api.saveResearch(username, merchantId, researchRating)
        } else {
            api.saveResearchOld(username, merchantId, researchRating)
        }
    }

    /**
     Fluxo User Status Prepago
     */
    fun getUserStatusPrepago(accessToken: String): Observable<PrepaidResponse> {
        return api.getUserPrepaidInformation(accessToken)
    }

    fun fetchUserCardBalance(
        cardProxy: String,
        accessToken: String,
    ): Observable<PrepaidBalanceResponse> = api.fetchUserCardBalance(cardProxy, accessToken)

    fun getCreditCardsStatement(
        initialDt: String,
        finalDt: String,
        pageSize: Int,
        pageNumber: Int,
        merchantId: String?,
        accessToken: String,
        proxyCard: String,
    ): Observable<CreditCardStatement> {
        return api.getCreditCardsStatement(
            proxyCard,
            initialDt,
            finalDt,
            pageSize,
            pageNumber,
            merchantId,
            accessToken,
        )
    }

    fun activateCard(
        merchantId: String,
        accessToken: String,
        serialNumber: String,
    ): Observable<Response<Void>> {
        return api.activateCard(merchantId, accessToken, serialNumber)
    }

    fun activateCardCateno(
        proxy: String,
        cardActivation: CardActivationCatenoRequest,
        token: String,
        xAuthorization: String,
    ): Observable<Response<Void>> = api.activateCatenoCard(proxy, cardActivation, token, xAuthorization)

    fun sendDocumentCreate(
        merchantId: String,
        accessToken: String,
        imageDocument: ImageDocument,
    ): Observable<MessagePhoto> {
        return api.sendDocumentCreate(merchantId, accessToken, imageDocument)
    }

    fun sendDocumentUpdate(
        merchantId: String,
        accessToken: String,
        imageDocument: ImageDocument,
    ): Observable<MessagePhoto> {
        return api.sendDocumentUpdate(merchantId, accessToken, imageDocument)
    }

    fun loginMultichannel(loginMultichannelRequest: LoginMultichannelRequest): Observable<LoginObj> {
        return api.loginMultichannel(loginMultichannelRequest)
    }

    fun beginTransfer(
        cardProxy: String,
        accessToken: String,
        bankTransferRequest: BankTransferRequest,
    ): Observable<TransferResponse> {
        return api.beginTransfer(
            cardProxy,
            accessToken,
            bankTransferRequest = bankTransferRequest,
        )
    }

    fun confirmTransfer(
        cardProxy: String,
        accessToken: String,
        transferId: String,
        authorization: String,
    ): Observable<TransferConfirmationResponse> {
        return api.confirmTransfer(
            cardProxy,
            accessToken,
            transferId,
            authorization,
        )
    }

    // TODO chamar método de pagamentos
    fun createPayment(
        cardProxy: String,
        accessToken: String,
        paymentRequest: PrepaidPaymentRequest,
    ): Observable<PrepaidPaymentResponse> {
        return api.createPayment(
            cardProxy,
            accessToken,
            paymentRequest = paymentRequest,
        )
    }

    fun confirmPayment(
        cardProxy: String,
        paymentId: String,
        accessToken: String,
        transferAuthorization: String,
    ): Observable<PrepaidPaymentResponse> {
        return api.confirmPayment(
            cardProxy,
            paymentId,
            accessToken,
            transferAuthorization,
        )
    }

    fun getAllNotification(): Observable<NotificationResponse> {
        return api.getAllNotification()
    }

    fun getNotificationCount(): Observable<NotificationCountResponse> {
        return api.getNotificationCount()
    }

    fun registrationAccount(
        accountRegistrationPayLoadRequest: AccountRegistrationPayLoadRequest,
        inviteToken: String?,
        akamaiSensorData: String?,
    ): Observable<SelfRegistrationResponse> {
        return api.registrationAccount(
            accountRegistrationPayLoadRequest = accountRegistrationPayLoadRequest,
            inviteToken = inviteToken,
            akamaiSensorData = akamaiSensorData,
        )
    }

    fun getMerchat(
        authorization: String,
        token: String,
    ): Observable<UserOwnerResponse> {
        return api.getMerchant(authorization, token)
    }

    fun getBanks(): Observable<BanksResponse> {
        return api.getBanks()
    }

    fun getHelpCenter(): Observable<HelpCenterResponse> {
        return api.getHelpCenter()
    }

    fun setBorrow(
        token: String,
        contratarEmprestimo: ContratarEmprestimoRecebaMaisRequest,
        accesssToken: String,
    ): Observable<ContratacaoResponse> {
        return api.borrow(token, contratarEmprestimo, accesssToken)
    }

    fun summary(accessToken: String): Observable<ResumoResponse> {
        return api.summary(accessToken)
    }

    fun keepInterestOffer(
        offerId: String,
        accessToken: String,
        authorization: String,
    ): Observable<ContratacaoResponse> {
        return api.keepInterestOffer(offerId, accessToken, authorization)
    }

    fun migrationUser(
        migrationRequest: MigrationRequest,
        accessToken: String,
        authorization: String,
    ): Observable<MultichannelUserTokenResponse> {
        return api.migrationUser(migrationRequest, accessToken, authorization)
    }

    fun getUserMigration(accessToken: String): Observable<Response<Unit>> {
        return api.getUserMigration(accessToken)
    }

    fun impersonate(
        ec: String,
        token: String,
        type: String,
        fingerprint: ImpersonateRequest,
    ): Observable<Impersonate> {
        return api.impersonate(ec, token, type, fingerprint)
    }

    fun getMerchants(accessToken: String) = this.api.getMerchants(accessToken)

    fun addNewEc(
        objEc: BankAccountObj,
        otpCode: String,
    ) = this.api.addNewEc(objEc, otpCode)

    fun children(
        token: String,
        pageSize: Int?,
        pageNumber: Int?,
        searchCriteria: String?,
    ): Observable<HierachyResponse> {
        return api.children(token, pageSize, pageNumber, searchCriteria)
    }

    fun merchantSupplies(access_token: String): Observable<MerchantSuppliesResponde> {
        return api.merchantSupplies(access_token)
    }

    fun loadSuplies(
        accessToken: String,
        authorization: String,
    ): Observable<SupliesResponse> {
        return api.loadSuplies(accessToken, authorization)
    }

    fun merchantAddress(accessToken: String): Observable<MerchantAddressResponse> {
        return api.merchantAddress(accessToken)
    }

    fun merchantBuySupply(
        token: String,
        supplies: ArrayList<MerchantSupplyChosen>,
    ): Observable<MerchantBuySupplyChosenResponse> {
        return api.merchantBuySupply(token, supplies)
    }

    fun paymentLinkActive(
        token: String,
        size: Int,
        page: Int,
    ): Observable<PaymentLinkResponse> {
        return api.paymentLinkActive(token, size, page)
    }

    fun deleleLink(
        token: String?,
        linkId: DeleteLink,
    ): Completable {
        return api.deleteLink(token, linkId)
    }

    fun generateLink(
        token: String,
        request: CreateLinkBodyRequest,
    ): Observable<CreateLinkBodyResponse> {
        return api.generateLink(token, request)
    }

    fun getLinkOrders(
        token: String,
        linkId: String,
    ): Observable<LinkOrdersResponse> {
        return api.getLinkOrders(token, linkId)
    }

    fun getLinkOrder(
        token: String,
        orderId: String,
    ): Observable<Order> {
        return api.getLinkOrder(token, orderId)
    }

    fun activationCode(
        activationCode: String? = null,
        fingerprint: String? = null,
    ): Observable<PutValueResponse> {
        return api.activationCode(
            MfaEnrollmentRequest(
                activationCode = activationCode,
                fingerprint = fingerprint,
            ),
        )
    }

    fun postMerchantChallengeActivate(request: MerchantChallengerActivateRequest) = api.postMerchantChallengeActivate(request)

    fun refreshTokenMfa(
        accessToken: String?,
        refreshToken: String?,
    ) = api.refreshToken(accessToken, refreshToken, CYFMonitor.getSensorData())

    fun faqSubCategories(
        token: String,
        faqId: String,
    ) = api.faqSubCategories(token, faqId)

    fun faqCategories(
        imageType: String,
        accessToken: String,
    ): Observable<List<HelpCategory>> {
        return api.faqCategories(imageType, accessToken)
    }

    // novo login
    fun loadMe(token: String): Observable<MeResponse> {
        return api.loadMe(token)
    }

    fun loadMerchant(token: String): Observable<MCMerchantResponse> {
        return api.loadMerchant(token)
    }

    fun loadBrands(token: String): Observable<List<Solution>> {
        return api.loadBrands(token)
    }

    fun getDomiciles(
        protocol: String?,
        status: String?,
        page: Int?,
        pageSize: Int?,
    ): Observable<PaymentAccountsDomicile> = api.getDomiciles(protocol, status, page, pageSize)

    fun getFrequentQuestions(accesssToken: String): Observable<List<QuestionDataResponse>> {
        return api.getFrequentQuestions(accesssToken)
    }

    fun faqQuestions(
        accessToken: String,
        faqId: String,
        subcategyId: String,
    ) = api.faqQuestions(accessToken, faqId, subcategyId)

    fun getFaqQuestionsByName(
        accessToken: String,
        tag: String,
    ) = api.getFaqQuestionsByName(accessToken, tag)

    fun getQuestionDetails(
        accessToken: String,
        faqId: String,
        subcategyId: String,
        questionId: String,
    ): Observable<QuestionDataResponse> =
        api.getQuestionDetails(
            accessToken,
            faqId,
            subcategyId,
            questionId,
        )

    fun likeQuestion(
        accessToken: String,
        faqId: String,
        subcategyId: String,
        questionId: String,
    ): Observable<QuestionReactionResponse> = api.likeQuestion(accessToken, faqId, subcategyId, questionId)

    fun dislikeQuestion(
        accessToken: String,
        faqId: String,
        subcategyId: String,
        questionId: String,
    ): Observable<QuestionReactionResponse> = api.dislikeQuestion(accessToken, faqId, subcategyId, questionId)

    fun getFaqContacts(accessToken: String) = api.getFaqContacts(accessToken)

    fun sendProtocol(ombudsman: OmbudsmanRequest) = api.sendProtocol(ombudsman)

    fun loadStatusPlan(token: String): Observable<TaxaPlanosStatusPlanResponse> {
        return api.loadStatusPlan(token)
    }

    fun getPlanInformation(planName: String): Observable<PlanInformationResponse> {
        return api.getPlanInformation(planName)
    }

    fun loadOverview(
        token: String,
        type: String,
    ): Observable<TaxaPlanosOverviewResponse> {
        return api.loadOverview(token, type)
    }

    fun getOfferIncomingFastDetail() = api.getOfferIncomingFastDetail()

    fun loadMarchine(token: String): Observable<TaxaPlanosSolutionResponse> {
        return api.loadMarchine(token)
    }

    fun loadMerchantSolutionsEquipments(token: String): Observable<TerminalsResponse> {
        return api.loadMerchantSolutionsEquipments(token)
    }

    fun loadPlanDetails(planName: String) = this.api.loadPlanDetais(planName)

    fun postOrdersReplacements(
        token: String,
        orderRequest: OrderReplacementRequest,
    ) = this.api.postOrdersReplacements(token, orderRequest)

    fun putMerchantOwner(
        accessToken: String,
        otpCode: String,
        owner: Owner,
    ): Observable<Response<Void>> {
        return api.putMerchantOwner(
            accessToken,
            otpCode,
            owner.cpf,
            PutEditOwnerRequest(owner.email, owner.phones),
        )
    }

    fun putMerchantContact(
        accessToken: String,
        otpCode: String,
        contact: Contact,
    ): Observable<Response<Void>> {
        return api.putMerchantContact(
            accessToken,
            otpCode,
            contact.id,
            PutEditContactRequest(contact.name, contact.types, contact.email, contact.phones),
        )
    }

    fun loadReceitaFederal(accessToken: String): Observable<ReceitaFederalResponse> {
        return api.loadReceitaFederal(accessToken)
    }

    fun saveReceitaFederal(accessToken: String): Observable<ReceitaFederalResponse> {
        return api.saveReceitaFederal(accessToken)
    }

    fun updateUserAddress(
        accessToken: String,
        otpCode: String,
        updateAddressRequest: AddressUpdateRequest
    ): Observable<Response<Void>> {
        return api.updateUserAddress(
            accessToken,
            otpCode,
            updateAddressRequest.id,
            updateAddressRequest,
        )
    }

    fun getAddressByCep(accessToken: String, cep: String): Observable<AddressResponse> {
        return api.getAddressByCep(accessToken, cep)
    }

    fun transferOfBrands(
        token: String,
        otpCode: String,
        transferFlag: FlagTransferRequest,
    ) = api.transferOfBrands(token, otpCode, transferFlag)

    fun domicilioTransferAccount(
        addFlag: AccountTransferRequest,
        token: String,
        otpGenerated: String? = null,
    ): Observable<Response<Void>> {
        return api.domicilioTransferAccount(token, otpGenerated, addFlag)
    }

    fun loadSolutionsOffers(
        token: String,
        imageType: String,
    ): Observable<MachineListOffersResponse> {
        return api.loadSolutionsOffers(imageType, token)
    }

    fun fetchAddressByCep(
        accessToken: String,
        cep: String,
    ): Observable<CepAddressResponse> {
        return api.fetchAddressByCep(accessToken, cep)
    }

    fun postOrders(
        token: String,
        orderRequest: OrderRequest,
    ) = this.api.postOrders(token, orderRequest)

    fun loadOrdersAvailability(token: String) = this.api.loadOrdersAvailability(token)

    fun verificationEmailConfirmation(token: String?) = this.api.verificationEmailConfirmation(token)

    fun resendEmail(token: String?) = this.api.resendEmail(token)

    fun getSummarySalesOnline(
        accessToken: String,
        authorization: String,
        initialDate: String? = null,
        finalDate: String? = null,
        cardBrand: List<Int>? = null,
        paymentType: List<Int>? = null,
        terminal: List<String>? = null,
        status: List<Int>? = null,
        cardNumber: Int? = null,
        nsu: String? = null,
        authorizationCode: String? = null,
        page: String? = null,
        pageSize: Int? = null,
    ) = this.api.getSummarySalesOnline(
        accessToken,
        authorization,
        initialDate,
        finalDate,
        cardBrand,
        paymentType,
        terminal,
        status,
        cardNumber,
        nsu,
        authorizationCode,
        page,
        pageSize,
    )

    fun getSummarySalesHistory(
        accessToken: String,
        authorization: String,
        type: String,
        initialDate: String? = null,
        finalDate: String? = null,
        cardBrands: List<Int>? = null,
        paymentTypes: List<Int>? = null,
    ) = this.api.getSummarySalesHistory(
        accessToken,
        authorization,
        type,
        initialDate,
        finalDate,
        cardBrands,
        paymentTypes,
    )

    fun getCardBrands(
        accessToken: String,
        authorization: String,
    ) = this.api.getCardBrands(accessToken, authorization)

    fun getPaymentTypes(
        accessToken: String,
        authorization: String,
        initialDate: String,
        finalDate: String,
    ) = this.api.getPaymentTypes(accessToken, authorization, initialDate, finalDate)

    fun getSummarySales(
        accessToken: String,
        authorization: String,
        initialDate: String,
        finalDate: String,
        initialAmount: Double?,
        finalAmount: Double?,
        customId: String?,
        saleCode: String?,
        truncatedCardNumber: String?,
        cardBrands: List<Int>?,
        paymentTypes: List<Int>?,
        terminal: List<String>?,
        status: List<Int>?,
        cardNumber: Int?,
        nsu: String?,
        authorizationCode: String?,
        page: Int?,
        pageSize: Int?,
    ) = this.api.getSummarySales(
        accessToken,
        authorization,
        initialDate,
        finalDate,
        initialAmount,
        finalAmount,
        customId,
        saleCode,
        truncatedCardNumber,
        cardBrands,
        paymentTypes,
        terminal,
        status,
        cardNumber,
        nsu,
        authorizationCode,
        page,
        pageSize,
    )

    /**
     * método que retorna se a venda tem saldo para ser cancelada
     * */
    fun balanceInquiry(
        item: Sale,
        token: String,
        data: String,
    ) = api.balanceInquiry(
        item.cardBrandCode.toString(),
        item.authorizationCode.toString(),
        item.nsu.toString(),
        item.terminal.toString(),
        item.truncatedCardNumber.toString(),
        data,
        data,
        item.paymentTypeCode.toString(),
        item.grossAmount.toString(),
        1,
        25,
        Utils.authorization(),
        token,
    )

    /**
     * método que envia uma venda para api para ser cancelada
     * */
    fun sendSaleToCancel(
        sales: ArrayList<RequestCancelApi>,
        currentOtpGenerated: String,
        token: String,
    ): Observable<ResponseCancelVenda> = api.sendSaleToCancel(sales, currentOtpGenerated, token, Utils.authorization())

    fun getCanceledSales(
        accessToken: String,
        sellsCancelParametersRequest: SellsCancelParametersRequest,
        page: Long?,
        pageSize: Int,
    ): Observable<ResultSummaryCanceledSales> {
        return api.getCanceledSells(
            accessToken,
            sellsCancelParametersRequest.initialDate,
            sellsCancelParametersRequest.finalDate,
            page,
            pageSize,
            sellsCancelParametersRequest.nsu,
            sellsCancelParametersRequest.saleAmount,
            sellsCancelParametersRequest.refundAmount,
            sellsCancelParametersRequest.paymentTypes,
            sellsCancelParametersRequest.cardBrands,
            sellsCancelParametersRequest.authorizationCode,
            sellsCancelParametersRequest.tid,
        )
    }

    fun filterCanceledSells(
        accessToken: String,
        initialDate: String,
        finalDate: String,
    ): Observable<ResultPaymentTypes> {
        return api.filterCanceledSells(accessToken, initialDate, finalDate)
    }

    fun getOthersMenu(accessToken: String): Observable<AppMenuResponse?> {
        return api.getOthersMenu(accessToken)
    }

    fun callMotoboy(orderId: String) = api.callMotoboy(orderId)

    fun checkEnrollment() = api.checkEnrollment()

    fun checkMfaEligibility() = api.checkMfaEligibility()

    fun resendMfa(request: MfaResendRequest?) = api.resendMfa(request)

    fun getMfaBanks() = api.getMfaBanks()

    fun sendMFABankChallenge(account: MfaAccount) = api.sendMFABankChallenge(account)

    fun postBankEnrollment(account: MfaAccount) = api.postBankEnrollment(request = account)

    fun resendCallMotoboy(orderId: String) = api.resendCallMotoboy(orderId)

    fun getReceivablesBankAccounts(
        initialDate: String,
        finalDate: String,
    ) = api.getReceivablesBankAccounts(initialDate, finalDate)

    fun onLoadAlerts(): Observable<AlertsResponse> = api.onLoadAlerts()

    fun onGeneratePdfAlerts(): Observable<FileResponse> = api.onGeneratePdfAlerts()

    fun offers(token: String) = api.offers(token)

    fun getHiringOffers() = api.getHiringOffers()

    fun postTermoAceite(bannerId: Int) = api.postTermoAceite(bannerId)

    fun simulation(
        offerId: String,
        loanAmount: BigDecimal,
        firstInstallmentDt: String,
        token: String,
    ) = api.simulation(token, offerId, loanAmount, firstInstallmentDt)

    fun fetchContracts(token: String) = api.fetchContracts(token)

    fun getContractDetails(token: String) = api.getContractDetails(token)

    fun getEligibleToOffer() = api.getEligibleToOffer()

    fun avaiableReceivableFilters(quickFilter: QuickFilter): Observable<FilterReceivableResponse> {
        return api.avaiableReceivableFilters(
            quickFilter.initialDate,
            quickFilter.finalDate,
        )
    }

    fun fetchMachineOpenedOrders(page: Int): Observable<OrderMachineResponse> {
        return api.fetchMachineOpenedOrders(page)
    }

    fun getOrderAffiliationDetail(orderId: Int) = this.api.getOrderAffiliationDetail(orderId)

    fun callDeleteRecebaRapido() = api.callDeleteRecebaRapido()

    fun postEnrollmentActivate(code: String) = api.postEnrollmentActivate(code)

    fun fetchEnrollmentActiveBank(): Observable<EnrollmentBankResponse> {
        return api.fetchEnrollmentActiveBank()
    }

    fun getLgpdEligibility() = api.getLgpdElegibility()

    fun postLgpdAgreement() = api.postLgpdAgreement()

    fun getMerchantPermissionsEligible(): Observable<MerchantPermissionsEligible> = api.getMerchantPermissionsEligible()

    fun sendPermisionRegister(): Observable<Response<Void>> = api.sendPermisionRegister()

    fun balcaoRecebiveisPermissionRegister(): Observable<MerchantResponseRegisterGet> = api.balcaoRecebiveisPermissionRegister()

    fun getDebitoContaPermissionsEligible(): Observable<ResponseDebitoContaEligible> = api.getDebitoContaPermissionsEligible()

    fun pixEligibility(): Observable<ResponseEligibilityPix> = api.pixElegibility()

    fun sendTerm(): Observable<Response<Void>> = api.sendTerm()

    fun sendTermPixPartner(): Observable<Response<Void>> = api.sendTermPixPartner()

    fun sendDebitoContaPermission(optin: String): Observable<Response<Void>> = api.sendDebitoContaPermission(optin)

    fun pixDataQuery(): Observable<ResponsePixDataQuery> = api.pixDataQuery()

    fun callDirf(
        year: Int,
        cnpj: String,
        companyName: String,
        owner: String,
        cpf: String,
        type: String,
    ) = api.callDirf(type, year, cnpj, companyName, owner, cpf)

    fun callDirfPDFOrExcel(
        year: Int,
        type: String?,
    ) = api.callDirfPDFOrExcel(type, year)

    fun loadNegotiations(
        initDate: String,
        finalDate: String,
    ): Observable<Negotiations> = api.loadNegotiations(initDate, finalDate)

    fun loadNegotiationsByType(
        page: Int,
        pageSize: Int,
        initDate: String,
        finalDate: String,
        type: String,
        quickFilter: QuickFilter?,
    ): Observable<Negotiations> =
        api.loadNegotiationsByFilter(
            page,
            pageSize,
            initDate,
            finalDate,
            type,
            quickFilter?.operationNumber,
            quickFilter?.initialAmount,
            quickFilter?.finalAmount,
            quickFilter?.merchantId,
        )

    fun getUnitReceivable(
        page: Int,
        pageSize: Int,
        negotiationDate: String,
        operationNumber: String,
        initialReceivableDate: String?,
        finalReceivableDate: String?,
        identificationNumber: String?,
        options: ArrayList<Int>,
    ) = api.getUnitReceivable(
        page,
        pageSize,
        negotiationDate,
        operationNumber,
        initialReceivableDate,
        finalReceivableDate,
        identificationNumber,
        options,
    )

    fun loadNegotiationsBanks(
        initDate: String,
        finalDate: String,
        type: String,
    ): Observable<NegotiationsBanks> = api.loadNegotiationsBanks(initDate, finalDate, type)

    fun loadFiltroVendasUnitariasBrands(
        date: String,
        identificationNumber: String,
    ): Observable<VendasUnitariasFilterBrands> = api.loadFiltroVendasUnitariasBrands(date, identificationNumber)

    fun getUserAdditionalInfo(): Observable<GetUserAdditionalInfo> = api.getUserAdditionalInfo()
}
