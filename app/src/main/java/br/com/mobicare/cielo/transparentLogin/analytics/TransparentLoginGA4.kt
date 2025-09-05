package br.com.mobicare.cielo.transparentLogin.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values

class TransparentLoginGA4 {
    fun logScreenView(screenName: String) {
        GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logButtonClick(screenName: String, contentName: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                Navigation.CONTENT_NAME to contentName
            )
        )
    }

    fun logException(screenPath: String, errorDescription: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                Exception.DESCRIPTION to errorDescription
            )
        )
    }

    companion object {
        const val TRANSPARENT_LOGIN_LOADING = "/login/primeiro_acesso/cadastro/validacao_id/tudo_certo/aguarde"
        const val TRANSPARENT_LOGIN_ERROR = "/login/primeiro_acesso/cadastro/validacao_id/erro_login"
        const val TRANSPARENT_LOGIN_ERROR_ACTION = "ir_para_o_login"
        const val UNAVAILABLE_SERVICE = "servico_indisponivel"
        const val INCOMPLETE_INFORMATION = "infos_incompletas_para_login"
    }
}