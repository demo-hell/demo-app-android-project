package br.com.mobicare.cielo.main.presentation.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase

class ServicesGA4 {

    fun logScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(SCREEN_VIEW_SERVICES)
    }

    fun logServiceSelectContent(contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_SERVICES,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_COMPONENT to CONTENT_COMPONENT_SERVICES,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logServiceClick(contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_SERVICES,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    companion object {
        const val SCREEN_VIEW_SERVICES = "/servicos"
        const val CONTENT_COMPONENT_SERVICES = "servicos"
        const val SERVICE = "/servico"
        const val REQUEST_MATERIALS = "solicitar_materiais"
    }

}