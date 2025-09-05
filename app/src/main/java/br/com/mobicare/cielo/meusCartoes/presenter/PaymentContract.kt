package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferAuthorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse

interface PaymentContract {

    interface View: IAttached {
        fun initView()
        fun initData()
        fun showUnauthorizedAndClose()
        fun showWrongInputDataError()
        fun showUnavaibleServer()
        fun showError(errorMessage: ErrorMessage)
        fun getNumberAndDateBarcode(barcode: String)
        fun finishCreatePayment(prepaidPaymentResponse: PrepaidPaymentResponse)
        fun responseSucess()
        fun showProgress()
        fun hideProgress()
        fun logout(errorMessage: ErrorMessage)
    }

    interface Presenter : CommonPresenter {
        fun createPayment(cardProxy: String?,
                          paymentRequest: PrepaidPaymentRequest)

        fun confirmPayment(cardProxy: String?,
                           paymentId: String?,
                           transferAuthorization: TransferAuthorization)

        fun validationBarcode(barcode: String): String
        fun validationValueCollectionOrTicket(barcode: String): String
        fun validationValueDateMaturity(barcode: String): String?
        fun validationNameBank(barcode: String): String?
        fun validationNameAgreement(barcode: String): String?
        fun validationBarcodeValid(barcode: String): Boolean?
    }

}