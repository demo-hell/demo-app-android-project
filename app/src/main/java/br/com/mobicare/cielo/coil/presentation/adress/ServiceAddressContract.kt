package br.com.mobicare.cielo.coil.presentation.adress

import br.com.mobicare.cielo.coil.domain.MerchantAddress
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import java.util.*

interface ServiceAddressContract {

    interface Presenter : BasePresenter<View> {
        fun loadAdress()
        fun buySupples()
        fun setSupplies(myList: ArrayList<CoilOptionObj>)
        fun onCleared()
        fun resubmit()
    }

    interface View : BaseView, IAttached {
        fun showAddress(response: MerchantAddress)
        fun showSucess(response: MerchantBuySupplyChosenResponse)
        fun showSubmit(error: ErrorMessage)
    }
}