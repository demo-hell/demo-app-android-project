package br.com.mobicare.cielo.pagamentoLink.presentation.presenter

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.LinkRequest
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO

interface LinkContract {

    interface BaseView {
        fun showLoading()
        fun hideLoading()
        fun onLogout() {}
        fun showError(errorMessage: ErrorMessage? = null) {}
    }

    interface CreateLinkView : BaseView {

        fun errorOnLinkCreation(errorMessage: ErrorMessage)
        fun linkSuccessfulCreated(createdLink: CreateLinkBodyResponse)
        fun setLabelButton(@StringRes resId: Int) {}
        fun goToShippingMethod(dto: PaymentLinkDTO) {}
        fun showIneligibleUser(errorMessage: ErrorMessage?)
        fun showPeriodicityCharge() {}
    }

    interface CreateLinkPresenter : CommonPresenter {
        fun setPaymentLinkDTO(dto: PaymentLinkDTO)
        fun generateLinkWithObjectDelivery(paymentLinkDto: PaymentLinkDTO?)
        fun generateLink(token: String, linkToCreate: LinkRequest)
        fun setFilter(quickFilter: QuickFilter? = null) {}
    }

}
