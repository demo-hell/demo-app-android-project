package br.com.mobicare.cielo.mfa.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.SIGNUP_EVENT
import br.com.mobicare.cielo.commons.analytics.SUCESSO

class MfaAnalyticsGA4 {

    fun logScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(SCREEN_VIEW_MFA_SUCCESS)
    }

    fun logSignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = SIGNUP_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SCREEN_VIEW_MFA_SUCCESS,
                UserAndImpersonate.STEP to SUCESSO,
            )
        )
    }

    companion object {
        const val SCREEN_VIEW_MFA_SUCCESS = "/app/troca_device/selfie/dispositivo_configurado/sucesso"
    }
}