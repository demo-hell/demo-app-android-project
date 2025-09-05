package br.com.mobicare.cielo.centralDeAjuda.search.analytics

import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.commons.analytics.Action.BUSCAR
import br.com.mobicare.cielo.commons.analytics.Action.CLIQUE
import br.com.mobicare.cielo.commons.analytics.Action.DETALHE
import br.com.mobicare.cielo.commons.analytics.Action.LIMPAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category.APP_CIELO
import br.com.mobicare.cielo.commons.analytics.Label.ERRO
import br.com.mobicare.cielo.commons.analytics.Label.OUTROS
import br.com.mobicare.cielo.commons.analytics.MAX_LENGTH

class HelpCenterSearchAnalytics {

    fun logSearch(term: String, resultsCount: Int) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, CENTRAL_DE_AJUDA, BUSCADOR),
            action = listOf(OUTROS, CENTRAL_DE_AJUDA, BUSCAR),
            label = listOf(term, "$resultsCount $RESULTADOS")
        )
    }

    fun logSearchClearClick(term: String, resultsCount: Int) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, CENTRAL_DE_AJUDA, BUSCADOR),
            action = listOf(OUTROS, CENTRAL_DE_AJUDA, BUSCAR, LIMPAR, CLIQUE),
            label = listOf(term, "$resultsCount $RESULTADOS")
        )
    }

    fun logSearchError(error: String, errorCode: String) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, CENTRAL_DE_AJUDA, BUSCADOR),
            action = listOf(OUTROS, CENTRAL_DE_AJUDA, BUSCAR, ERRO),
            label = listOf(errorCode, error)
        )
    }

    fun logSearchResultItemClick(term: String, item: FrequentQuestionsModelView) {
        val idLength = DIVIDER_LENGTH + item.id.length
        Analytics.trackEvent(
            category = listOf(APP_CIELO, CENTRAL_DE_AJUDA, BUSCADOR),
            action = listOf(OUTROS, CENTRAL_DE_AJUDA, BUSCAR, DETALHE, CLIQUE),
            label = listOf(term.take(MAX_LENGTH - idLength), item.id)
        )
    }

    companion object {
        const val CENTRAL_DE_AJUDA = "central de ajuda"
        const val BUSCADOR = "buscador"
        const val RESULTADOS = "resultados"

        const val DIVIDER_LENGTH = 3
    }
}