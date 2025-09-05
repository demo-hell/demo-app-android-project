package br.com.mobicare.cielo.lighthouse.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import io.reactivex.Scheduler

class LightHousePresenter(val lightHouseView: LightHouseContract.View,
                          val uiScheduler: Scheduler,
                          val ioScheduler: Scheduler) : LightHouseContract.Presenter {

    private val compositeDisposableHandler = CompositeDisposableHandler()

    override fun callLightHouse() {

        if (UserPreferences.getInstance().statusFarol) {
            lightHouseView.showLightHouseBannerToRegister()
        } else {
            lightHouseView.showLightHouseProductScreen()
        }

    }

    override fun onResume() {
        compositeDisposableHandler.start()
    }

    override fun onDestroy() {
        compositeDisposableHandler.destroy()
    }
}