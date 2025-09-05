package br.com.mobicare.cielo.taxaPlanos.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase

class FeesPlansGA4 {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logClick(
        screenName: String,
        contentName: String,
        contentType: String = BUTTON,
        contentComponent: String? = null
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap = listOfNotNull(
                SCREEN_NAME to screenName,
                CONTENT_TYPE to contentType,
                CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                contentComponent?.let { CONTENT_COMPONENT to contentComponent }
            ).toMap()
        )
    }

    fun logDisplayContent(screenName: String, contentComponent: String?, contentType: String) {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = DISPLAY_CONTENT_EVENT,
                eventsMap = listOfNotNull(
                    SCREEN_NAME to screenName,
                    CONTENT_TYPE to contentType,
                    contentComponent?.let { CONTENT_COMPONENT to contentComponent }
                ).toMap()
            )
        }

    companion object {
        private const val SERVICES = "servicos"
        const val AUTOMATIC_RECEIVE = "recebimento_automatico"
        private const val SCREEN_VIEW_RA = "$SERVICES/$AUTOMATIC_RECEIVE"
        const val BOTH = "ambas"
        private const val FEES_PLANS = "taxas_planos"
        const val CANCEL_RA = "cancelar_recebimento_automatico"
        const val CONFIRM_CANCEL_RA = "confirmar_cancelamento_recebimento_automatico"
        const val CANCEL = "cancelamento"
        const val YES_I_WANT_TO_CANCEL = "sim_desejo_cancelar"
        const val SCREEN_VIEW_HOME = "/$SCREEN_VIEW_RA"
        const val SCREEN_VIEW_RA_CANCEL = "/$FEES_PLANS/$AUTOMATIC_RECEIVE/$CANCEL"
        const val FEES_AND_PLANS = "/outros/taxas_e_planos/meu_plano"
        const val UPDATE_INCOMING_SALES = "alterar_recebimento_de_vendas"
        const val CONTENT_INCOMING_SALES = "recebimento"
    }
}