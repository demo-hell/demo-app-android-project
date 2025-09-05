package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse

interface PostecipadoMeuAluguelContract {

    interface View : BaseView {
        fun showRentInformation(response: PlanInformationResponse, rentAmountValue: Double?)
        fun notEligibleForPostecipate()
        fun unavailableService(message: String?)
    }

    interface Presenter {
        fun loadRentInformation(plansSolutionsResponse: TaxaPlanosSolutionResponse?)
        fun onStop()
        fun onResume()
    }
}