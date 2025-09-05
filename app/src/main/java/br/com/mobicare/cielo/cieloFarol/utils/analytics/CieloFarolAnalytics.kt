package br.com.mobicare.cielo.cieloFarol.utils.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label

class CieloFarolAnalytics {

    fun logCieloFarolAccessButtonClick() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(CIELO_FAROL_NAME, INDICATOR_STATUS, Action.CLIQUE),
            label = listOf(Label.BOTAO, ACCESS_CIELO_FAROL)
        )
    }

    companion object {
        const val CIELO_FAROL_NAME = "cielo farol"
        const val INDICATOR_STATUS = "acompanhamento de indicadores"
        const val ACCESS_CIELO_FAROL = "acessar cielo farol"
    }
}