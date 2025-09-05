package br.com.mobicare.cielo.taxaPlanos.presentation.ui.main

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.api.PlanListener

interface TaxaPlanosMainContract {

    interface Presenter {
        fun onPause()
        fun onResume()
        fun loadData()
    }

    interface View : IAttached, LogoutListener, PlanListener {
        fun onError(error: ErrorMessage? = null)
        fun showResult(response: TaxaPlanosStatusPlanResponse, machine: TaxaPlanosSolutionResponse?)
        fun showLoading()
        fun hideLoading()
        fun visibilityHeader(isVisible: Boolean)
    }
}