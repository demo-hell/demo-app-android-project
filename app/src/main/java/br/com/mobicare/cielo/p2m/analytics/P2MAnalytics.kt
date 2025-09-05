package br.com.mobicare.cielo.p2m.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.pix.constants.EMPTY

class P2MAnalytics {

    fun logScreenView(name: String, className: Class<Any>) {
        Analytics.trackScreenView(screenName = name, screenClass = className)
    }

    fun logScreenActions(
        actionName: String,
        flowName: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, P2M_ANALITYCS),
            action = listOf(actionName, flowName, Action.CLIQUE),
            label = listOf(componentName, labelName)
        )
    }

    companion object {
        const val P2M_ANALITYCS = "p2m"
        const val P2M_ANALITYCS_CLOSE = "fechar"
        const val P2M_ANALITYCS_HELP = "ajuda"
        const val P2M_ANALITYCS_BACK = "voltar"
        const val P2M_ANALITYCS_NEXT = "proximo"
        const val P2M_ANALITYCS_OPEN_WHATS_APP_BNS = "abrir whatsapp business"
        const val P2M_ANALITYCS_END_REGISTER = "finalize o cadastro"
        const val P2M_ANALITYCS_RECEIPT_DEADLINE = "prazo de recebimento das vendas por whatsapp"
        const val P2M_ANALITYCS_RECEIPT_DEADLINE_TWO_DAYS = "receba em 2 dias"
        const val P2M_ANALITYCS_RECEIPT_DEADLINE_THIRTY_DAYS = "receba em 30 dias"
        const val SCREENVIEW_P2M_END_REGISTER_ON_WHATS_APP = "/cielo p2m/credenciamento/finalize o cadastro no whatsapp business"
        const val SCREENVIEW_P2M_CHOOSE_FORM_OF_RECEIPT = "/cielo p2m/credenciamento/escolha da forma recebimento"
    }
}