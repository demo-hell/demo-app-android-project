package br.com.mobicare.cielo.recebaMais.presentation.presenter

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.recebaMais.domain.LoanSimulationResponse
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.domains.entities.Contract

interface UserLoanContract {

    interface Presenter {
        fun onStart()
        fun onPause()
    }

    interface View {

        interface Banner : View {
            fun availableOffersToShow(offer: Offer?)
        }

        interface UserLoanScreen : View {
            fun showLoading()
            fun hideLoading()

            fun showSimulation()
            fun showSimulationWithResponseOffer(offer: Offer?)
            fun showPendingContract(contracts: List<Contract>)
            fun showNotApproved()
            fun showWaitingForApproval(contract: Contract)
            fun showSummaryContract()
            fun showNetworkError()
            fun showErrorHandler(errorMessage: ErrorMessage)
            fun showContractErrorHandler(errorMessage: ErrorMessage)
            fun showContractInternalError()
            fun unauthorized()
        }

        interface Simulation : View {
            fun simulationResult(loanSimulationResponse: LoanSimulationResponse?)
            fun showLimitExceededError()

            fun showNetworkError()
            fun showError()

            fun showLoading()
            fun hideLoading()
        }
    }
}