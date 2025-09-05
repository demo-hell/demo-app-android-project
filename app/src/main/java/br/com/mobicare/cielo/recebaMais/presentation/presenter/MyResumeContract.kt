package br.com.mobicare.cielo.recebaMais.presentation.presenter

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetails

interface MyResumeContract {

    interface View: BaseView {
        fun showContract(contract: ContractDetails)
    }

    interface Presenter {
        fun loadDetails()
        fun onStart()
        fun onDestroy()
    }
}