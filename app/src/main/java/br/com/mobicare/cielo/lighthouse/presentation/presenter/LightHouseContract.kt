package br.com.mobicare.cielo.lighthouse.presentation.presenter

import br.com.mobicare.cielo.commons.presentation.CommonPresenter

interface LightHouseContract {

    interface Presenter : CommonPresenter {
        fun callLightHouse()
    }

    interface View {

        fun showLightHouseBannerToRegister()
        fun showLightHouseProductScreen()

    }

}