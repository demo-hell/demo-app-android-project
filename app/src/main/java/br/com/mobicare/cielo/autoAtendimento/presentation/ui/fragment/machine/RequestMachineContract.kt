package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse

interface RequestMachineContract {

    interface Presenter {
        fun loadOffers(typeDensity: String)
        fun onCleared()
    }

    interface View : BaseView,  IAttached {
        fun showOffers(response: MachineListOffersResponse)
    }

}