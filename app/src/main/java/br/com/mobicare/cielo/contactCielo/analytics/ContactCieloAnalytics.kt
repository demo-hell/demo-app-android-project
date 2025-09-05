package br.com.mobicare.cielo.contactCielo.analytics

import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Elegible.IS_ELEGIBLE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.constants.HelpCenter
import br.com.mobicare.cielo.contactCielo.domain.model.ContactCieloWhatsapp
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as GA4Tracking

object ContactCieloAnalytics {

    private const val WHATSAPP_BUTTON = "duvida_fale_com_a_cielo"
    private const val SCREEN_VIEW_HOME = "/home"
    private const val SCREEN_VIEW_HOME_DUVIDA = "/home/duvida_fale_com_a_Cielo"

    fun trackWhatsappButtonClick() {
        GA4Tracking.trackEvent(
            CLICK_EVENT, mapOf(
                SCREEN_NAME to SCREEN_VIEW_HOME,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to WHATSAPP_BUTTON
            )
        )
    }

    fun trackDisplayButtonsOnBottomSheetContactCielo(contactCieloWhatsapp: ContactCieloWhatsapp) {
        GA4Tracking.trackEvent(
            DISPLAY_CONTENT_EVENT, mapOf(
                SCREEN_NAME to SCREEN_VIEW_HOME,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to CieloApplication.context.getString(contactCieloWhatsapp.contentName),
                CONTENT_COMPONENT to IS_ELEGIBLE
            )
        )
    }

    fun trackCloseClickButtonOnBottomSheetContactCielo() {
        GA4Tracking.trackEvent(
            CLICK_EVENT, mapOf(
                SCREEN_NAME to SCREEN_VIEW_HOME_DUVIDA,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to FECHAR
            )
        )
    }

    fun trackClickButtonOnBottomSheetContactCielo(contactCieloWhatsapp: ContactCieloWhatsapp) {
        GA4Tracking.trackEvent(
            CLICK_EVENT, mapOf(
                SCREEN_NAME to SCREEN_VIEW_HOME_DUVIDA,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to CieloApplication.context.getString(contactCieloWhatsapp.contentName)
            )
        )
    }

    fun trackClickCentralAjudaButtons(contactCieloWhatsapp: ContactCieloWhatsapp) {
        GA4Tracking.trackEvent(
            CLICK_EVENT, mapOf(
                SCREEN_NAME to HelpCenter.HELP_CENTER,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to CieloApplication.context.getString(contactCieloWhatsapp.contentName)
            )
        )
    }
}