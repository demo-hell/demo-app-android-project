package br.com.mobicare.cielo.chargeback.analytics

import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import java.text.Normalizer
import java.util.regex.Pattern
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4
class ChargebackGA4 {

    fun logScreenView(screenName: String) {
        ga4.trackScreenView(screenName)
    }

    fun logDisplayContentPendingChargeback(
        modal: String,
        contentComponent: String,
        description: String
    ){
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mutableMapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS,
                CONTENT_TYPE to modal,
                CONTENT_COMPONENT to contentComponent,
                DESCRIPTION to description
        )
        )
    }

    fun logDisplayContentTreatedChargebacks(
    modal: String,
    contentComponent: String,
    description: String
    ){
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mutableMapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS,
                CONTENT_TYPE to modal,
                CONTENT_COMPONENT to contentComponent,
                DESCRIPTION to description
            )
        )
    }

    fun logDisplayContentPendingOrTreatedChargebacks(
        screenName: String,
        modal: String,
        contentComponent: String,
        description: String
    ){
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mutableMapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                CONTENT_TYPE to modal,
                CONTENT_COMPONENT to contentComponent,
                DESCRIPTION to description
            )
        )
    }

    fun logException(screenName: String, error: NewErrorMessage?) {
        val description = error?.let { it.message.ifEmpty { it.flagErrorCode } }
            ?: EMPTY
        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to error?.httpCode.toStringOrEmpty()
            )
        )
    }

    companion object{
        private const val SALES = "vendas"
        private const val CONTESTATION = "contestacao"
        private const val PENDING = "pendentes"
        private const val DETAILS = "detalhes"
        private const val REFUSE = "recusar"
        private const val SUCESS = "sucesso"
        private const val ACCEPT = "aceitar"
        private const val TREATED = "tratadas"

        const val CHARGEBACK_SALES_PENDING = "contestacao_de_vendas_pendentes"
        const val CHARGEBACK_SALES_TREATED = "contestacao_de_vendas_tratadas"
        const val INFO_SALES_CONTESTATION = "informacoes_da_venda_contestada"

        const val SCREEN_VIEW_CHARGEBACK_PENDING = "/$SALES/$CONTESTATION/$PENDING"
        const val SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS = "/$SALES/$CONTESTATION/$PENDING/$DETAILS"
        const val SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE = "/$SALES/$CONTESTATION/$PENDING/$REFUSE"
        const val SCREEN_VIEW_CHARGEBACK_PENDING_REFUSE_SUCESS = "/$SALES/$CONTESTATION/$PENDING/$REFUSE/$SUCESS"
        const val SCREEN_VIEW_CHARGEBACK_PENDING_ACCEPT = "/$SALES/$CONTESTATION/$PENDING/$ACCEPT"
        const val SCREEN_VIEW_CHARGEBACK_PENDIND_ACCEPT_SUCESS = "/$SALES/$CONTESTATION/$PENDING/$ACCEPT/$SUCESS"
        const val SCREEN_VIEW_CHARGEBACK_TREATED = "/$SALES/$CONTESTATION/$TREATED"
        const val SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS = "/$SALES/$CONTESTATION/$TREATED/$DETAILS"
    }
}