package br.com.mobicare.cielo.login.firstAccess.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.*

class FirstAccessAnalytics {
    fun logScreenView(className: Class<Any>, screenName: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun logDisplayContent(screenName: String, code: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to getErrorDescription(code),
                Navigation.CONTENT_TYPE to GA_WARNING_EVENT
            )
        )
    }

    private fun getErrorDescription(code: String): String {
        return when (code) {
            REQUEST_ADM_PERMISSION.errorType -> GA_ERROR_ACCESS_OWNER
            REQUEST_MANAGER_PERMISSION.errorType -> GA_ERROR_ACCESS_MANAGER
            else -> return code
        }
    }

    companion object {
        const val GA_FIRST_ACCESS_CLIENT_PATH = "/login/primeiro_acesso/ja_sou_cliente"
        const val GA_AUTO_REGISTER_PATH = "/autocadastro/primeiro_acesso"
        const val GA_AUTO_REGISTER_PASSWORD_PATH = "/autocadastro/primeiro_acesso/senha"
        const val GA_AUTO_REGISTER_SUCCESS_PATH = "/autocadastro/primeiro_acesso/sucesso"
        const val GA_WARNING_EVENT = "warning"
        const val GA_PASSWORD_EVENT = "senha"
        const val INCORRECT_CPF = "cpf_incorreto"
        const val INCORRECT_EMAIL = "email_incorreto"
        const val GA_ERROR_ACCESS_MANAGER = "erro_solicite_acesso_ao_gestor"
        const val GA_ERROR_ACCESS_OWNER = "erro_solicite_acesso_ao_proprietario"
    }
}