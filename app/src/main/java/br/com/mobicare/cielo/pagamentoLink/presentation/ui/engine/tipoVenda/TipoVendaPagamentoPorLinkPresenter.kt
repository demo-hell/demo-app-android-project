package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference

class TipoVendaPagamentoPorLinkPresenter(
    private val view: TipoVendaPagamentoPorLinkContract.View,
    private val featureTogglePreference: FeatureTogglePreference
) : TipoVendaPagamentoPorLinkContract.Presenter {

    override fun verifyProductDeliveryFeature() {
        featureTogglePreference
            .getFeatureTogle(FeatureTogglePreference.SUPERLINK_ENTREGA)
            .let { showSuperLinkDelivery ->
                view.onShowProductDeliveryOption(showSuperLinkDelivery)
            }
    }

}