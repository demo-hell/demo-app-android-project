package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse

interface BankAccountToTransferInputContract {

    interface View : IAttached {
        fun initView()
        fun initData()
        fun nextStep(transferResponse: TransferResponse)
        fun transferSuccess(successMessage: String)
        fun wrongTransfer(message: String)
        fun showProgress()
        fun hideProgress()
        fun updateButtonNextState(enabled: Boolean)
        fun errorFields()

        fun unavaiableAmount()

        fun showError()

        fun showWrongInputDataError()
        fun showUnavaibleServer()
        fun showUnauthorizedAndClose()


        //métodos do bottomSheetDialog
        fun showWrongExpirationDate()
        fun showEmptyCvv()
        fun logout(errorMessage: ErrorMessage)
    }

    interface Presenter : CommonPresenter {
        fun beginTransfer(cardProxy: String?,
                          avaiableAmount: Double,
                          bankTransferRequest: BankTransferRequest)
    }
}