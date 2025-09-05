package br.com.mobicare.cielo.home.presentation.main

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.main.domain.Menu

interface MenuContract {
    interface View : BaseView {
        fun showMenu(menu: List<Menu>)

    }
}