package br.com.mobicare.cielo.home.presentation.main

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.main.domain.Menu

interface HomeServiceContract {
    interface View : BaseView {
        fun onServiceClick(homeService: Menu)
    }
}