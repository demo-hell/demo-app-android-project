package br.com.mobicare.cielo.superlink.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label

class SuperLinkAnalytics {

    fun logShowBottomSheetNotEligible(){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SUPER_LINK),
            action = listOf(Action.MODAL, Action.EXIBICAO),
            label = listOf(SUPER_LINK_DISABLE)
        )
    }

    fun logClickSuperLinkRequest(labelBottom: String){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SUPER_LINK),
            action = listOf(Action.MODAL, SUPER_LINK_DISABLE, Action.CLIQUE),
            label = listOf(Label.BOTAO, labelBottom)
        )
    }

    fun gaSendButtonTooltip(labelButton: String, functionality: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, functionality),
            action = listOf(Action.HEADER),
            label = listOf(Label.TOOLTIP, labelButton)
        )
    }

    fun sendGaScreenView(screen: String) {
        Analytics.trackScreenView(
            screenName = screen,
            screenClass = this.javaClass
        )
    }

    fun sendGaNewPaymentButton(labelButton: String) {
        sendGaButton(listOf(labelButton), Action.NOVO_PAGAMENTO)
    }

    fun sendGaLinkTypeButton(labelButton: String) {
        sendGaButton(listOf(labelButton), Action.TIPO_DE_LINK)
    }

    fun sendGaButton(labelList: List<String>, action: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.BOTAO, action),
            label = labelList
        )
    }

    fun sendGaShare(labelButton: String, linkType: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(linkType),
            label = listOf(Action.COMPARTILHAR, labelButton)
        )
    }

    companion object {
        const val SUPER_LINK = "super link"
        const val SUPER_LINK_DISABLE = "super link nao habilitado"
        const val SOLICITAR_AGORA = "solicitar agora"
        const val PAGAMENTO_POR_LINK = "pagamento por link"
        const val LIXEIRA = "lixeira"
        const val DICAS_DE_SEGURANCA = "dicas de seguranca"
        const val GENERATE_LINK = "gerar link"
        const val VALOR_FRETE = "valor frete"
        const val VALOR_DA_VENDA = "valor da venda"
        const val FRETE_FIXO = "frete fixo"
        const val NOME_DO_PRODUTO = "nome do produto"
        const val PESO_DO_PRODUTO = "peso do produto"
        const val LIBERADO = "liberado"
        const val CREATE_NEW_LINK = "criar novo link"
        const val SEND_PRODUCT = "enviar um produto"
        const val CHARGE_VALUE = "cobrar valor"
        const val ENTREGA_CORREIO = "entrega correio"
        const val DETALHES_DO_PRODUTO = "detalhes do produto"
        const val LOGGI_DADOS_DE_COLETA = "dados de coleta loggi"
        const val CEP = "CEP"
        const val HELP_CENTER = "central de ajuda"
        const val GENERATED_LINK = "link gerado"
        const val LINK_FOR_PAYMENT = "link para pagamento"

        const val NOT_ELIGIBLE_SCREEN = "/pagamento-por-link/criar-novo-link/entre-em-contato"
        const val LINK_PAYMENT_SCREEN = "/pagamento-por-link"
        const val LINK_TYPE_SCREEN = "/pagamento-por-link/super-link/tipo-de-link"
        const val GENERATED_LINK_SCREEN = "/pagamento-por-link/super-link/criar-novo-link/link-gerado"
    }

}

