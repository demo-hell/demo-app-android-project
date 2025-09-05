package br.com.mobicare.cielo.onboardingWeb.bridges

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.OnboardingWeb
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil
import br.com.mobicare.cielo.forgotMyPassword.presentation.ForgotMyPasswordNavigationFlowActivity
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants
import br.com.mobicare.cielo.selfieChallange.presentation.SelfieChallengeActivity
import br.com.mobicare.cielo.selfieChallange.utils.SelfieCameraSDK
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeError
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeParams
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeResult
import br.com.mobicare.cielo.selfieChallange.utils.SelfieOperation
import br.com.mobicare.cielo.transparentLogin.presentation.TransparentLoginActivity
import java.io.Serializable

interface UnicoJavascriptCallback {
    fun execute(status: String, faceidToken: String)
}

class UnicoSdkBridge {
    private var context: Context
    private var componentActivity: ComponentActivity
    private var webview: WebView
    private lateinit var selfieChallengeLauncher: ActivityResultLauncher<Intent>

    private var mAllowMeContextualLocal: AllowMeContextual = AllowMeContextual()
    private var allowMeHash: String = ""
    private var allowMeError: Boolean = false
    private var datadogEvent: DatadogEvent

    constructor(context: Context, activity: ComponentActivity, webview: WebView) {
        this.context = context
        this.componentActivity = activity
        this.webview = webview
        this.datadogEvent  = DatadogEvent(context, UserPreferences.getInstance())
        this.startAllowMe()
        this.createSelfieChallengeLauncher()


        this.webview.postDelayed({
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: inicialização da webview",
                key = "postDelayed",
                value = "this.webview.postDelayed"
            )
            this.webview.evaluateJavascript(
                """
                    window.bridge_unicosdk = {
                        start: function(document, operationType, callback) {
                            window.bridge_unicosdk_static.start(document, operationType);
                            window.startCallback = function(status, response) {
                                callback(status, response)
                                delete window.startCallback
                            }
                        },
                        goBack: function() {
                            window.bridge_unicosdk_static.goBack();
                        },
                        completeFlow: function(cpf, password, ec) {
                            window.bridge_unicosdk_static.completeFlow(cpf, password, ec);
                        },
                        goToForgotPassword: function() {
                            window.bridge_unicosdk_static.goToForgotPassword();
                        },
                        checkApplicationState: function() {
                            window.bridge_unicosdk_static.checkApplicationState();
                        },
                        close: function() {
                            window.bridge_unicosdk_static.close();
                        },
                        logClickEvent: function(screenName, buttonName) {
                            window.bridge_unicosdk_static.logClickEvent(screenName, buttonName);
                        },
                        logDisplayContent: function(screenName, contentType, description) {
                            window.bridge_unicosdk_static.logDisplayContent(screenName, contentType, description);
                        },
                        logScreenView: function(screenName) {
                            window.bridge_unicosdk_static.logScreenView(screenName);
                        },
                        logException: function(screenName, description, statusCode){
                            window.bridge_unicosdk_static.logException(screenName, description, statusCode);
                        },
                        logAnalyticsAF: function(event, screenName){
                            window.bridge_unicosdk_static.logAnalyticsAF(event, screenName);
                        }
                    }
                """, null
            )
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: inicialização da webview",
                key = "evaluateJavascript",
                value = "this.webview.evaluateJavascript"
            )
        }, OnboardingWeb.SAFE_DELAYTIME_JSINJECTION)
    }

    private fun startAllowMe() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento: Bridge",
            key = "startAllowMe",
            value = "Inicialização AllowMe"
        )
        mAllowMeContextualLocal.apply {
            start(context)
            collect(context, { result ->
                allowMeHash = result
                allowMeError = false
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: Bridge",
                    key = "startAllowMeSucess",
                    value = "Inicialização AllowMe Sucesso"
                )
            }) { errorMessage ->
                allowMeError = true
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: Bridge",
                    key = "startAllowMeError",
                    value = "Inicialização AllowMe Erro"
                )
            }
        }
    }

    private fun selfieChallengeError(intent: Intent): SelfieChallengeError? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR,
                SelfieChallengeError::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR) as SelfieChallengeError?
        }
    }

    private fun selfieChallengeSuccess(intent: Intent): SelfieChallengeResult? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS,
                SelfieChallengeResult::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS) as SelfieChallengeResult?
        }
    }

    private fun createSelfieChallengeLauncher() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento: createSelfieChallengeLauncher",
            key = "createSelfieChallengeLauncher",
            value = "Inicialização createSelfieChallengeLauncher"
        )
        selfieChallengeLauncher = this.componentActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> selfieChallengeSuccessCallback(it)
                else -> selfieChallengeErrorCallback(it)
            }
        }
    }

    private fun selfieChallengeSuccessCallback(result: ActivityResult) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento: sucesso na selfie",
            key = "selfieChallengeSuccessCallback",
            value = "Sucesso createSelfieChallengeLauncher"
        )
        result.data?.let { data ->
            selfieChallengeSuccess(data)?.let { selfieChallengeResult ->
                val faceIdToken = selfieChallengeResult.faceIdToken.orEmpty()
                val photo64 = selfieChallengeResult.photo64.orEmpty()
                val imageFileType = selfieChallengeResult.imageFileType
                val response = "{\"faceidToken\": \"$faceIdToken\", \"fingerprint\": $allowMeHash, \"photo64\": \"$photo64\", \"imageFileType\": \"$imageFileType\"}"
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: preparando dados de sucesso da selfie para envio para o javascript",
                    key = "selfieChallengeSuccessCallback",
                    value = "FaceIdToken: $faceIdToken, ImageFiletype: $imageFileType, AllowMeHash: $allowMeHash, Photo64: $photo64"
                )
                sendStartResultToJavascript("success", response)
            }
        } ?:  sendStartResultToJavascript("error", "{\"message\": \"selfieChallengeSuccessCallback\"}")
    }

    private fun selfieChallengeErrorCallback(result: ActivityResult) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento: erro na selfie",
            key = "selfieChallengeErrorCallback",
            value = "Erro createSelfieChallengeLauncher"
        )
        result.data?.let { data ->
            selfieChallengeError(data)?.let { selfieError ->
                val errormsg = selfieError.type.toString()
                val response = "{\"message\": \"$errormsg\"}"
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: preparando dados de erro da selfie para envio para o javascript",
                    key = "selfieChallengeErrorCallback",
                    value = "Erro: ${selfieError.message}"
                )
                sendStartResultToJavascript("error", response)
            }
        } ?: sendStartResultToJavascript("error", "{\"message\": \"selfieChallengeErrorCallback\"}")
    }

    private fun sendStartResultToJavascript (response: String, payload: String) {
        Handler(Looper.getMainLooper()).post {
            this.webview.evaluateJavascript(
                "startCallback('$response', '$payload')",
                null
            )
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: dados da selfie erro/sucesso integrado a webview",
                key = "sendStartResultToJavascript",
                value = "Dados: $response"
            )
        }
    }

    inner class JavascriptInterface {
        @android.webkit.JavascriptInterface
        fun start(document: String, operationType: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Incializar captura ativado pelo javascript",
                key = "JavascriptInterface.Start",
                value = "Dados: $operationType"
            )
            if(allowMeError) {
                val errormsg = "ALLOWME_HASH_ERROR"
                val response = "{\"message\": \"$errormsg\"}"
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento:Erro no Allowme, enviando para webview",
                    key = "JavascriptInterface.Start.allowMeError",
                    value = "Dados: $response"
                )
                Handler(Looper.getMainLooper()).post {
                    webview.evaluateJavascript(
                        "startCallback('success', '$response')",
                        null
                    )
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Allowme, enviando para webview",
                        key = "JavascriptInterface.Start.Handler",
                        value = "startCallback('success', '$response')"
                    )
                }
                return
            }

            val intent = Intent(context, SelfieChallengeActivity::class.java).apply {
                val faceIdPartner = SelfieCameraSDK.UNICO
                val selfieParam = SelfieChallengeParams(
                    username = document,
                    cameraSDK = faceIdPartner,
                    operation = SelfieOperation.CRD_FACE_ID
                )
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: Preparando SDK UNICO",
                    key = "Intent.selfieParam",
                    value = "SelfieParams: $document,  -  $faceIdPartner, -  $operationType"
                )
                putExtra(
                    SelfieChallengeConstants.SELFIE_CHALLENGE_PARAMS,
                    selfieParam as Serializable
                )
            }
            selfieChallengeLauncher.launch(intent)
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Start Unico Nativo",
                key = "selfieChallengeLauncher.launch(intent)",
                value = "Nativo Iniciado"
            )
        }

        @android.webkit.JavascriptInterface
        fun goToForgotPassword() {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Start esqueceu sua senha acionado pela webview",
                key = "goToForgotPassword",
                value = "Esqueceu a senha acionado"
            )
            val intent = Intent(context, ForgotMyPasswordNavigationFlowActivity::class.java)
            componentActivity.startActivity(intent)
        }

        @android.webkit.JavascriptInterface
        fun checkApplicationState(): Boolean {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Start checkApplicationState",
                key = "checkApplicationState",
                value = "checkApplicationState value =  false"
            )
            return false
        }

        @android.webkit.JavascriptInterface
        fun goBack() {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Método voltar acionado pela webview",
                key = "goBack",
                value = "goBack - voltando para o contexto anterior"
            )
            componentActivity.finish()
        }

        @android.webkit.JavascriptInterface
        fun close() {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Método fechar (finish) acionado pela webview",
                key = "close",
                value = "close - fecha a activity atual"
            )
            componentActivity.finish()
        }

        @android.webkit.JavascriptInterface
        fun completeFlow(cpf: String, password: String, ec: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Método para iniciar login transparente acionado pela webview",
                key = "completeFlow",
                value = "Dados:  CPF -  $cpf, EC - $ec"
            )
            Intent(context, TransparentLoginActivity::class.java).apply {
                putExtra("cpf", cpf)
                putExtra("password", password)
                putExtra("ec", ec)
                componentActivity.startActivity(this)
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento: Método para iniciar login transparente acionado pela webview",
                    key = "completeFlowToTransparentLoginActivity",
                    value = "Dados:  CPF -  $cpf, EC - $ec"
                )
            }
        }

        @android.webkit.JavascriptInterface
        fun logClickEvent(screenName: String, buttonName: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: logClickEvent acionado pela webview",
                key = "logClickEvent",
                value = "Dados:  $screenName, $buttonName"
            )
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
                eventsMap = mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                    GoogleAnalytics4Events.Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                    GoogleAnalytics4Events.Navigation.CONTENT_NAME to buttonName,
                )
            )
        }

        @android.webkit.JavascriptInterface
        fun logScreenView(screenName: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: logScreenView acionado pela webview",
                key = "logScreenView",
                value = "Dados:  $screenName"
            )
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = GoogleAnalytics4Events.ScreenView.SCREEN_VIEW_EVENT,
                isLoginOrImpersonateFlow = false,
                eventsMap = mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                )
            )
        }

        @android.webkit.JavascriptInterface
        fun logDisplayContent(screenName: String, contentType: String, description: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: logDisplayContent acionado pela webview",
                key = "logDisplayContent",
                value = "Dados:  $screenName, $contentType, $description"
            )
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT,
                eventsMap = mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                    GoogleAnalytics4Events.Navigation.CONTENT_TYPE to contentType,
                    GoogleAnalytics4Events.Exception.DESCRIPTION to description,
                )
            )
        }

        @android.webkit.JavascriptInterface
        fun logException(screenName: String, description: String, statusCode: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: logDisplayContent acionado pela webview",
                key = "logException",
                value = "Dados:  $screenName, $description, $statusCode"
            )
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
                eventsMap = mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                    GoogleAnalytics4Events.Exception.DESCRIPTION to description,
                    GoogleAnalytics4Events.Exception.STATUS_CODE to statusCode,
                )
            )
        }

        @android.webkit.JavascriptInterface
        fun logAnalyticsAF(event: String, screenName: String) {
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: logDisplayContent acionado pela webview",
                key = "logAnalyticsAF",
                value = "Dados:  $event, $screenName"
            )
            val eventParams = mapOf(AppsFlyerConstants.AF_SCREEN_NAME to screenName)
            AppsFlyerUtil.send(context, event, eventParams)
        }
    }
}