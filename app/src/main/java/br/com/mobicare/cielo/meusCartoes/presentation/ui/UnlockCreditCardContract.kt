package br.com.mobicare.cielo.meusCartoes.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached

interface UnlockCreditCardContract {

    interface Presenter {

        fun startCardActivation(serialNumber: String)
        fun setView(unlockCreditCardView: UnlockCreditCardView)
    }

    interface UnlockCreditCardView : IAttached {
        fun showLoading()
        fun hideLoading()

        fun showError(error: ErrorMessage)

        fun showInvalidCardNumber()
        fun showSuccessActivation()
        fun logout(message: String)

    }

}