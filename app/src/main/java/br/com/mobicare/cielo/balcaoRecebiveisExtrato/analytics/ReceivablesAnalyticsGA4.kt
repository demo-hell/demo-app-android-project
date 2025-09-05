package br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.CIELO_NEGOTIATION
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.DETAILS_OF_OPERATION
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase

class ReceivablesAnalyticsGA4 {

    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logException(screenName: String, error: NewErrorMessage? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to (error?.message.takeUnless {
                    it.equals(DEFAULT_ERROR_MESSAGE)
                } ?: error?.flagErrorCode.orEmpty()).normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to error?.httpCode?.toString().orEmpty(),
            )
        )
    }

    fun logDisplayContent(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = listOfNotNull(
                ScreenView.SCREEN_NAME to screenName,
                CONTENT_TYPE to MODAL,
                CONTENT_COMPONENT to CIELO_NEGOTIATION,
                Exception.DESCRIPTION to DETAILS_OF_OPERATION
            ).toMap()
        )
    }
}