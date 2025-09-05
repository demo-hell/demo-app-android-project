package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy

import android.app.Dialog

interface SolicitationMotoboyView {

    fun loadParams() {}
    fun initView() {}
    fun responseMotoboy(
            responseMotoboy: ResponseMotoboy,
            resendMotoboy: Boolean = false
    ) {
    }

    fun expiredSession() {}
    fun serverError() {}
    fun enhance() {}
    fun callLoadMotoboy(resendMotoboy: Boolean = false) {}
    fun responseCallMotoboy(responseMotoboy: ResponseMotoboy) {}
    fun callBottonSheetGeneric(isShowModalMotoboy: Boolean = true) {}
    fun closeDialog() {}
    fun notFound() {}
    fun screenLocated(motoboy: ResponseMotoboy) {}
    fun collectionCanceled() {}
    fun displayedChild(value: Int) {}
    fun openTrackingUrl(trackingUrl: String){}
    fun delayCloseLocatesScreen(dialog: Dialog){}
    fun navigateUp()
}