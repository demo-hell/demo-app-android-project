package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO

interface DeliveryContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(error: ErrorMessage)
        fun linkGenerated(response: CreateLinkBodyResponse, gaDeliveryType: String?)
        fun showAlert(error: ErrorMessage)
    }

    interface Presenter {
        fun setPaymentLinkDTO(dto: PaymentLinkDTO)
        fun onChoiceButtonClicked(option: String)
        fun onNextButtonClicked(weight: String? = null, cep: String? = null, price: String? = null)
        fun setFilter(quickFilter: QuickFilter?)
    }
}