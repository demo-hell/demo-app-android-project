package br.com.mobicare.cielo.taxaPlanos.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics

object FeeAndPlansGA4 {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    object ScreenView {
        private const val SCREEN_VIEW_FEE_AND_PLANS = "/outros/taxas_e_planos"
        const val SCREEN_VIEW_FEE_AND_PLANS_MY_PLAN = "$SCREEN_VIEW_FEE_AND_PLANS/meu_plano"
        const val SCREEN_VIEW_FEE_AND_PLANS_MY_RENT = "$SCREEN_VIEW_FEE_AND_PLANS/meu_aluguel"
    }
}
