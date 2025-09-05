package br.com.mobicare.cielo.coil.presentation.choose

import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import java.util.*

interface CoilChooseContract {

    interface Presenter : BasePresenter<View> {
        fun loadSupplies()
        fun onClieared()
    }

    interface View : BaseView, IAttached {
        fun showSupplies(supplies: ArrayList<CoilOptionObj>)
        fun showIneligible(message: String)
    }
}