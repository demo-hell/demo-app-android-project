package br.com.mobicare.cielo.mySales.analytics
import br.com.mobicare.cielo.commons.analytics.Action.CANCELAR
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.BEGIN_CANCEL_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCELLATION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCELLATIONS
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCELLATION_REASON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCEL_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCEL_SALE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.MY_CANCELLATIONS
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.SELECT_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.Label.SUCESSO
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4
class MySalesGA4 {

    fun logScreenView(screenName: String) {
        ga4.trackScreenView(screenName)
    }

    fun selectContent(contentName: String){
        ga4.trackEvent(
            eventName = SELECT_CONTENT_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to SCREEN_NAME_SALES_MADE,
                CONTENT_TYPE to BUTTON,
                CONTENT_COMPONENT to CANCELLATIONS,
                CONTENT_NAME to contentName
                ))
    }

    fun beginCancel(screenName: String, contentType: String) {
        ga4.trackEvent(
            eventName = BEGIN_CANCEL_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to screenName,
                CONTENT_TYPE to contentType,
                CONTENT_NAME to CANCEL_SALE
                )
        )
    }

    fun cancel(){
        ga4.trackEvent(
            eventName = CANCEL_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to SCREEN_NAME_CANCEL_SUCESS,
                CANCELLATION_REASON to CANCEL_SALE
            )
        )
    }

    fun logException(screenName: String, newErrorMessage: NewErrorMessage? = null, errorMessage: ErrorMessage? = null){
        val description = (newErrorMessage?.message?.ifEmpty { newErrorMessage.flagErrorCode }
            ?: errorMessage?.message?.ifEmpty { errorMessage.errorCode }
            ?: Text.EMPTY).normalizeToLowerSnakeCase()
        val statusCode = (newErrorMessage?.httpCode ?: errorMessage?.httpStatus)?.toStringOrEmpty() ?: Text.EMPTY
        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                GoogleAnalytics4Events.Exception.STATUS_CODE to statusCode)
        )
    }

    companion object{

        const val SALES = "vendas"
        const val SALES_MADE = "vendas_realizadas"
        const val SALES_DETAILS = "detalhes_da_venda"

        const val SCREEN_NAME_SALES_MADE = "/$SALES/$SALES_MADE"
        const val SCREEN_NAME_SALES_DETAILS = "/$SALES/$SALES_MADE/$SALES_DETAILS"
        const val SCREEN_NAME_CANCELLATION_CANCEL = "/$SALES/$CANCELLATION/$CANCELAR"
        const val SCREEN_NAME_CANCEL_SUCESS = "/$SALES/$CANCELLATION/$CANCELAR/$SUCESSO"
        const val SCREEN_NAME_CANCELLATION_MY_CANCELLATIONS = "/$SALES/$CANCELLATION/$MY_CANCELLATIONS"
    }
}