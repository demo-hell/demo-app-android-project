package br.com.mobicare.cielo.webView.analytics

import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

private const val WEB_PAGE_SUCCESS = "/{code}/abertura/sucesso"
private const val WEB_PAGE_ERROR = "/{code}/abertura"
private const val FLOW_NAME_PREFIX = "APP_ANDROID_MENU_"
private const val CODE_PLACEHOLDER = "{code}"

class WebViewContainerAnalytics {
    fun logScreenSuccess(flowName: String) {
        ga4.trackScreenView(screenName = WEB_PAGE_SUCCESS.formatScreenName(flowName))
    }

    fun logException(flowName: String, description: String) {
        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to WEB_PAGE_ERROR.formatScreenName(flowName),
                Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
            )
        )
    }

    private fun String.formatScreenName(flowName: String): String {
        val formattedFlowName = flowName.removePrefix(FLOW_NAME_PREFIX).normalizeToLowerSnakeCase()
        return this.replace(CODE_PLACEHOLDER, formattedFlowName)
    }
}