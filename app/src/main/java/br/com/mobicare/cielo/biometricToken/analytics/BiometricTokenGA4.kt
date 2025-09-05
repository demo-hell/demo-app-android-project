package br.com.mobicare.cielo.biometricToken.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking.trackEvent
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.SIGNUP_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.STEP
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.utils.toLetterWithUnderScore
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4
import br.com.stoneage.identify.enums.LiveSelfieValidationError

class BiometricTokenGA4 {

    fun logScreenView(screenName: String) {
        GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logAuthorizeInstalationClick(){
        trackEvent(
            eventName = CLICK_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                SCREEN_NAME to SCREEN_VIEW_AUTHORIZATION,
                CONTENT_COMPONENT to AUTHORIZE_YOUR_DEVICE,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to ACCESS_LIMITED,
            )
        )
    }

    fun logDisplayContent(screenName: String, description: String, contentType: String) {
            trackEvent(
                eventName = DISPLAY_CONTENT_EVENT,
                eventsMap = mapOf(
                    SCREEN_NAME to screenName,
                    DESCRIPTION to description,
                    CONTENT_TYPE to contentType
                )
        )
    }

    fun logAuthorizeInstalationSignUp() {
        trackEvent(
            eventName = SIGNUP_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SCREEN_VIEW_AUTHORIZATION,
                STEP to AUTHORIZE_INSTALATION,
            )
        )
    }

    fun logTryAgainClick(){
        trackEvent(
            eventName = CLICK_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                SCREEN_NAME to SCREEN_VIEW_BIOMETRIC_RERUN,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to TRY_AGAIN,
            )
        )
    }

    fun logOpenCameraClick(screenName: String) {
        trackEvent(
            eventName = CLICK_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                SCREEN_NAME to screenName,
                CONTENT_COMPONENT to TAKE_SELFIE,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to OPEN_CAMERA,
            )
        )
    }

    fun logException(screenName: String, description: String, code: String){
        trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapFilterStatusCode(screenName, code, description.toLetterWithUnderScore())
        )
    }

    fun logExceptionErrorSelfie(screenName: String, description: LiveSelfieValidationError?){
        trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                SCREEN_NAME to screenName,
                Exception.DESCRIPTION to getErrorSelfie(description),
            )
        )
    }

    fun getErrorSelfie(description: LiveSelfieValidationError?) : String{
        return when(description) {
            LiveSelfieValidationError.IMAGE_NOT_GOOD_ENOUGH -> {
                ForgotMyPasswordGA4.IMAGE_NOT_GOOD_ENOUGH
            }
            LiveSelfieValidationError.NO_FACE_DETECTED -> {
                ForgotMyPasswordGA4.NO_FACE_DETECTED
            }
            LiveSelfieValidationError.TOO_MANY_FACES -> {
                ForgotMyPasswordGA4.TOO_MANY_FACES
            }
            LiveSelfieValidationError.TILTED_FACE ->{
                ForgotMyPasswordGA4.TILTED_FACE
            }
            LiveSelfieValidationError.WEARING_HAT -> {
                ForgotMyPasswordGA4.WEARING_HAT
            }
            LiveSelfieValidationError.WEARING_GLASSES -> {
                ForgotMyPasswordGA4.WEARING_GLASSES
            }
            LiveSelfieValidationError.WEARING_READING_GLASSES -> {
                ForgotMyPasswordGA4.WEARING_READING_GLASSES
            }
            LiveSelfieValidationError.WEARING_MASK -> {
                ForgotMyPasswordGA4.WEARING_MASK
            }
            LiveSelfieValidationError.FACE_NOT_CENTERED -> {
                ForgotMyPasswordGA4.FACE_NOT_CENTERED
            }
            LiveSelfieValidationError.FACE_TOO_FAR -> {
                ForgotMyPasswordGA4.FACE_TOO_FAR
            }
            LiveSelfieValidationError.FACE_IS_SMILING -> {
                ForgotMyPasswordGA4.FACE_IS_SMILING
            }
            LiveSelfieValidationError.FACE_TOO_CLOSE -> {
                ForgotMyPasswordGA4.FACE_TOO_CLOSE
            }
            LiveSelfieValidationError.FACE_TOO_BRIGHT -> {
                ForgotMyPasswordGA4.FACE_TOO_BRIGHT
            }
            LiveSelfieValidationError.FACE_TOO_DARK -> {
                ForgotMyPasswordGA4.FACE_TOO_DARK
            }
            LiveSelfieValidationError.FACES_DO_NOT_MATCH -> {
                ForgotMyPasswordGA4.FACES_DO_NOT_MATCH
            }
            else -> {
                ForgotMyPasswordGA4.UNKNOWN_ERROR
            }
        }
    }

    private fun mapFilterStatusCode(screenName: String, code: String, description: String): Map<String, Any> {
        var eventsMapValues = mapOf(
            SCREEN_NAME to screenName,
            Exception.DESCRIPTION to description.toLetterWithUnderScore(),
            Exception.STATUS_CODE to code,
        )
        if (code.isEmpty()){
            eventsMapValues = eventsMapValues.filterKeys { it != Exception.STATUS_CODE }
        }
        return eventsMapValues
    }

    companion object{
        const val SCREEN_VIEW_AUTHORIZATION = "/app/troca_device/autorizacao"
        const val SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE = "/app/troca_device/selfie/dicas"
        const val SCREEN_VIEW_SAFE_DEVICE = "/app/troca_device/selfie/dispositivo_seguro"
        const val SCREEN_VIEW_BIOMETRIC_RERUN = "/app/troca_device/selfie/reexecutar"
        const val SCREEN_VIEW_BIOMETRIC_ERROR = "/app/troca_device/erro"

        const val AUTHORIZE_YOUR_DEVICE =  "autorize_seu_dispositivo"
        const val ACCESS_LIMITED = "continuar_com_acesso_limitado"
        const val AUTHORIZE_INSTALATION = "autorizar_instalacao"
        const val TAKE_SELFIE = "tirar_uma_foto_do_rosto"
        const val OPEN_CAMERA = "abrir_camera"
        const val TRY_AGAIN = "tentar_novamente"
        const val ENABLE_ID = "habilite_id"
        const val VALIDATE_SELFIE = "/login/validacao_foto"
        const val ERROR_VALIDATE_SELFIE = "erro_na_validacao_da_foto"
    }
}