package br.com.mobicare.cielo.debitoEmConta.analytics

import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.REMOVE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.AUTHORIZATIONS
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.DEBIT_ACCOUNT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.OTHERS
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.Label.SUCCESS
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class DebitAccountGA4 {

    fun logScreenView(screenName: String) {
        ga4.trackScreenView(screenName)
    }

    fun click(screenName: String, contentName: String){
        ga4.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to screenName,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to contentName,
                CONTENT_COMPONENT to DEBIT_ACCOUNT
            )
        )
    }

    fun logException(screenName: String, error: ErrorMessage?) {
        val description = error?.let { it.message.ifEmpty { it.errorCode } }
            ?: Text.EMPTY
        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                GoogleAnalytics4Events.Exception.STATUS_CODE to error?.httpStatus.toStringOrEmpty()
            )
        )
    }

    companion object{

        const val REMOVE_AUTHORIZATION = "remover_autorizacao"
        const val TERMS_OF_USE = "termos_de_uso"

        const val SCREEN_NAME_OTHERS_AUTHORIZATIONS = "/$OTHERS/$AUTHORIZATIONS"
        const val SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_TERMS_OF_USE = "/$OTHERS/$AUTHORIZATIONS/$DEBIT_ACCOUNT/$TERMS_OF_USE"
        const val SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT = "/$OTHERS/$AUTHORIZATIONS/$DEBIT_ACCOUNT"
        const val SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE = "/$OTHERS/$AUTHORIZATIONS/$DEBIT_ACCOUNT/$REMOVE"
        const val SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE_SUCCESS = "/$OTHERS/$AUTHORIZATIONS/$DEBIT_ACCOUNT/$REMOVE/$SUCCESS"
    }
}