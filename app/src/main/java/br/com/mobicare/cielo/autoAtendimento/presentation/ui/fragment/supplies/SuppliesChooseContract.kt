package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.supplies

import br.com.mobicare.cielo.coil.domain.SupplyDTO
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface SuppliesChooseContract {

    interface View : BaseView, IAttached {
        fun showSupplies()
        fun buttonClick(listMerchants: List<SupplyDTO>?)
    }
}