package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio

import androidx.annotation.StringRes
import androidx.navigation.NavDirections
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO

interface FormaEnvioContract {
    interface View {
        fun showButton(@StringRes resId: Int)
        fun goToNextStep(navDirections: NavDirections, dto: PaymentLinkDTO, quickFilter: QuickFilter?) {}
        fun showLoggyState()
        fun showCorreiosState()
        fun showFreightState()
        fun showFreeState()
        fun showLoading()
        fun hideLoading()
        fun showError(error: ErrorMessage)
        fun enableNextButton(isEnabled: Boolean)
        fun showAlert(error: ErrorMessage)
        fun closeWindow()
        fun gaSendTypeDelivery(deliveryType: String)
        fun errorOnLinkCreation(errorMessage: ErrorMessage)
        fun linkSuccessfulCreated(createdLink: CreateLinkBodyResponse)
    }

    interface Presenter : CommonPresenter {
        fun setPaymentLinkDTO(dto: PaymentLinkDTO)
        fun onChoiceButtonClicked(option: String)
        fun onNextButtonClicked()
        fun setFilter(quickFilter: QuickFilter?)
        fun onInstructionButtonClicked()
        fun generateLinkWithObjectDelivery(paymentLinkDto: PaymentLinkDTO?)
    }
}