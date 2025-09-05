package br.com.mobicare.cielo.meusCartoes.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse

interface CreditCardsContract {

    interface Presenter {
        fun fetchCardInformation(isCardSuccess: Boolean)
        fun showBottomFragmentProcess(situationCode: String?, card: Card? = null, issuer : String = "")
        fun onResume()
        fun onPause()
    }

    interface CreditCardsView : IAttached {
        fun showLoading()
        fun hideLoading()

        fun showError()
        fun showCardsInformation(prepaidResponse: PrepaidResponse)
        fun showLastTransactions(proxyCard: String, myCardTransfer: Boolean, myCardPayment: Boolean)
        fun startCardActivation(issuer: String)

        fun startCardAccountFWD()
        fun startCardActivationFWD()
        fun startCardProcessingFWD()
        fun startCardReadProblemFWD()
        fun startCardProblemFWD()

        fun startCardSentSuccess()
        fun logout(errorMessage: ErrorMessage)
        fun showIneligible(message: String)
        fun showNotOwnerError(errorMessage: ErrorMessage)
        fun showPix(prepaidResponse: PrepaidResponse)
        fun showErrorAccessDeniedIssuer()
    }

    interface BankAccountView {
        fun showLoading()
        fun hideLoading()

        fun showError(errorMessage: ErrorMessage)
        fun fillSpinnerBanks(banks: List<Bank>)
        fun toggleAccountType(checkingAccountType: Boolean)

        fun updateEnabledNext(notEmptyInputs: Boolean)
        fun finishStep(bankTransactionData: BankTransferRequest)

        fun enrollmentError() {}
        fun userEnrollmentEligible() {}

        fun showUnauthorizedAndClose()
        fun showUnavailableServer()
    }
}