package br.com.mobicare.cielo.meusrecebimentosnew.analytics

import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class MyReceivablesGA4 {
    fun logScreenView(screenName: String, flow: String) {
        var screenNameFlow = screenName.replace(FLOW, flow)
        ga4.trackEvent(
            eventName = SCREEN_VIEW,
            eventsMap = mutableMapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenNameFlow
            )
        )
    }

    fun logException(screenName: String,  flow: String, error: ErrorMessage?) {
        var screenNameFlow = screenName.replace(FLOW, flow)
        val description = error?.let { it.message.ifEmpty { it.errorCode } }
            ?: Text.EMPTY
        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenNameFlow,
                GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                GoogleAnalytics4Events.Exception.STATUS_CODE to error?.httpStatus.toStringOrEmpty()
            )
        )
    }

    companion object{

        const val RECEIVABLES = "recebiveis"
        const val DETAIL = "detalhe"
        const val LIST = "lista"
        const val MORE_INFOS = "mais_infos"
        const val FLOW = "flow"

        val SCREEN_NAME_RECEIVABLES = "/$RECEIVABLES"
        val SCREEN_NAME_RECEIVABLES_DETAIL_LIST = "/$RECEIVABLES/$DETAIL/$LIST/$FLOW"
        val SCREEN_NAME_RECEIVABLES_DETAIL_MORE_INFOS = "/$RECEIVABLES/$DETAIL/$MORE_INFOS/$FLOW"
    }
}