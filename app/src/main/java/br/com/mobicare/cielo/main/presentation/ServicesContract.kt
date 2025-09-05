package br.com.mobicare.cielo.main.presentation

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.main.domain.Menu

interface ServicesContract {

    interface Presenter : CommonPresenter {
        fun getAvailableServices()
    }

    interface View: BaseView {

        fun showAvailableServices(menuElements: List<Menu>)
    }

}