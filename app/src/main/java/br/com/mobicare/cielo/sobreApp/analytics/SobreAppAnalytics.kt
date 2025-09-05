package br.com.mobicare.cielo.sobreApp.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics

object SobreAppAnalytics {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    object ScreenView {
        const val SCREEN_VIEW_OTHERS_ABOUT_APP = "/outros/sobre_o_app"
    }
}
