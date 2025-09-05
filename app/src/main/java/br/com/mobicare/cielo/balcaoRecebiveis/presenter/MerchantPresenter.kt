package br.com.mobicare.cielo.balcaoRecebiveis.presenter

import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.merchant.data.MerchantRepositoryImpl
import io.reactivex.Scheduler

class MerchantPresenter(
    val mainBottomNavigationView: MainBottomNavigationContract.View,
    val merchantRepositoryImpl: MerchantRepositoryImpl,
    val uiScheduler: Scheduler,
    val ioScheduler: Scheduler
) {


}