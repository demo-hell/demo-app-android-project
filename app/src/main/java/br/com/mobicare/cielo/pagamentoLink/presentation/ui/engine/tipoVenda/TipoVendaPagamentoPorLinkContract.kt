package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda

interface TipoVendaPagamentoPorLinkContract {
    interface View {
        fun onShowProductDeliveryOption(show: Boolean)
    }

    interface Presenter {
        fun verifyProductDeliveryFeature()
    }
}