package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics

import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class PredictiveBatteryAnalytics {
    fun logScreenView(screenPath: String) = ga4.trackScreenView(screenPath)

    fun logException(
        screenPath: String,
        error: NewErrorMessage?,
    ) {
        val description =
            error?.let {
                it.message.ifEmpty { it.flagErrorCode }
            } ?: Text.EMPTY

        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap =
                mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenPath,
                    GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                    GoogleAnalytics4Events.Exception.STATUS_CODE to error?.httpCode.toStringOrEmpty(),
                ),
        )
    }

    fun logException(
        screenPath: String,
        description: String,
    ) {
        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap =
                mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenPath,
                    GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                ),
        )
    }

    fun logClickButton(
        screenPath: String,
        labelButton: String,
    ) {
        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
            eventsMap =
                mapOf(
                    GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenPath,
                    GoogleAnalytics4Events.Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                    GoogleAnalytics4Events.Navigation.CONTENT_NAME to labelButton.normalizeToLowerSnakeCase(),
                ),
        )
    }

    companion object {
        private const val SUPPORT = "suporte"
        private const val BATTERY = "bateria"
        private const val ERRO = "erro"
        private const val AVAILABLE_SERVICE = "servico_disponivel"
        private const val REFUSE_SUPPORT = "recusar_suporte"
        private const val ACCEPT_SUPPORT = "aceitar_suporte"
        private const val REGISTRATION = "registro"
        private const val REQUEST_SENT = "solicitacao_enviada"

        const val UNAVAILABLE_SERVICE = "servico_indisponivel"
        const val LOGICAL_NUMBER_INVALID = "numero_logico_invalido"

        private const val SCREEN_VIEW_BASE = "/$SUPPORT/$BATTERY"
        const val SCREEN_VIEW_LOGICAL_NUMBER_INVALID = "$SCREEN_VIEW_BASE/$ERRO"
        const val SCREEN_VIEW_UNAVAILABLE_SERVICE = "$SCREEN_VIEW_BASE/$UNAVAILABLE_SERVICE"
        const val SCREEN_VIEW_AVAILABLE_SERVICE = "$SCREEN_VIEW_BASE/$AVAILABLE_SERVICE"
        const val SCREEN_VIEW_REFUSE_SUPPORT = "$SCREEN_VIEW_BASE/$REFUSE_SUPPORT"
        const val SCREEN_VIEW_REFUSE_SUPPORT_ERROR = "$SCREEN_VIEW_BASE/$REFUSE_SUPPORT/$ERRO"
        const val SCREEN_VIEW_ACCEPT_SUPPORT = "$SCREEN_VIEW_BASE/$ACCEPT_SUPPORT"
        const val SCREEN_VIEW_REQUEST_EXCHANGE_ERROR = "$SCREEN_VIEW_BASE/$REGISTRATION/$ERRO"
        const val SCREEN_VIEW_REQUEST_SENT = "$SCREEN_VIEW_BASE/$REGISTRATION/$REQUEST_SENT"
    }
}
