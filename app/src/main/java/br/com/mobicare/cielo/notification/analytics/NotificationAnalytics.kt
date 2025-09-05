package br.com.mobicare.cielo.notification.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.notification.analytics.NotificationAnalytics.ScreenView.SCREEN_VIEW_NOTIFICATION

object NotificationAnalytics {
    fun logScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(SCREEN_VIEW_NOTIFICATION)
    }

    object ScreenView {
        const val SCREEN_VIEW_NOTIFICATION = "/notificacoes"
    }
}
