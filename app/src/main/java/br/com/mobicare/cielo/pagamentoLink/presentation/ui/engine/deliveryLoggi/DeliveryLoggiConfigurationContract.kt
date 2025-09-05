package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.deliveryLoggi

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ProductDetailDTO

interface DeliveryLoggiConfigurationContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(error: ErrorMessage)
        fun showAlert(error: ErrorMessage)
        fun linkGenerated(response: CreateLinkBodyResponse)
    }

    interface Presenter {
        fun setPaymentLinkDTO(dto: PaymentLinkDTO)
        fun setFilter(quickFilter: QuickFilter?)
        fun onNextButtonClicked(dimension: ProductDetailDTO, weight: String)
    }
}