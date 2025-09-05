package br.com.mobicare.cielo.forgotMyPassword.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.EXCEPTION_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.utils.toLetterWithUnderScore

class ForgotMyPasswordGA4 {

    fun logScreenView(screenName: String) {
        GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logClick(screenName: String, contentName: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                SCREEN_NAME to screenName,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to contentName,
            )
        )
    }

    fun logDisplayContent(screenName: String, description: String, contentType: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to screenName,
                DESCRIPTION to description,
                CONTENT_TYPE to contentType
            )
        )
    }

    fun logException(screenName: String, description: String, code: String){

        var eventsMapValues = mapOf(
            SCREEN_NAME to screenName,
            DESCRIPTION to description.toLetterWithUnderScore(),
            Exception.STATUS_CODE to code,
        )
        if (code.isEmpty()){
            eventsMapValues = eventsMapValues.filterKeys { it != Exception.STATUS_CODE }
        }
        GoogleAnalytics4Tracking.trackEvent(
            eventName = EXCEPTION_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = eventsMapValues
        )
    }

    companion object {
        const val SCREEN_VIEW_FORGOT = "/app/login/esqueci_minha_senha"
        const val SCREEN_VIEW_FORGOT_CONFIRM_EMAIL = "/login/troca_de_senha/e_mail"
        const val SCREEN_VIEW_FORGOT_REDEFINITION_2 = "/app/login/troca_de_senha"
        const val SCREEN_VIEW_FORGOT_SELFIE_ERROR = "/app/login/esqueci_minha_senha/selfie/erro_selfie"
        const val SCREEN_VIEW_FORGOT_SELFIE_TIPS = "/app/login/validacao_id"
        const val SCREEN_VIEW_FORGOT_SELFIE_GENERIC_ERROR = "/app/login/esqueci_minha_senha/selfie/erro_generico"
        const val SCREEN_VIEW_FORGOT_SELFIE_SUCCESS = "/login/troca_de_senha/sucesso"
        const val SCREEN_NAME_LOGIN = "/login"
        const val SCREEN_NAME_FORGOT_PASSWORD = "/app/login/troca_de_senha"

        const val IMAGE_NOT_GOOD_ENOUGH = "image_not_good_enough"
        const val NO_FACE_DETECTED = "no_face_detected"
        const val TOO_MANY_FACES = "too_many_faces"
        const val TILTED_FACE = "tilted_face"
        const val WEARING_HAT = "wearing_hat"
        const val WEARING_GLASSES = "wearing_glasses"
        const val WEARING_READING_GLASSES = "wearing_reading_glasses"
        const val WEARING_MASK = "wearing_mask"
        const val FACE_NOT_CENTERED = "face_not_centered"
        const val FACE_TOO_FAR = "face_too_far"
        const val FACE_IS_SMILING = "face_is_smiling"
        const val FACE_TOO_CLOSE = "face_too_close"
        const val FACE_TOO_BRIGHT = "face_too_bright"
        const val FACE_TOO_DARK = "face_too_dark"
        const val FACES_DO_NOT_MATCH = "face_do_not_match"
        const val UNKNOWN_ERROR = "unknown_error"
        const val CHANGE_PASSWORD = "redefinir_senha"
        const val WARNING = "warning"
        const val INCORRECT_DATA = "dados_incorretos"
        const val PASSWORD = "senha"
    }
}