package br.com.mobicare.cielo.home.presentation.main

import android.content.Context
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.main.domain.Menu

interface OthersMenuContract {

    interface Presenter : CommonPresenter {

        fun getOthersMenu(accessToken: String)

    }


    interface View : BaseView {

        fun showOthersMenu(othersMenuResponse: List<Menu>)

    }


}