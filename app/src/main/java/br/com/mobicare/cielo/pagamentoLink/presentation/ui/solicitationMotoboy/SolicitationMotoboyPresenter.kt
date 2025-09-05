package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy

import android.app.Dialog

interface SolicitationMotoboyPresenter {

    fun callMotoboy(orderId: String)
    fun resendCallMotoboy(orderId: String)
    fun callLoadMotoboy()
    fun loadParams()
    fun initView()
    fun callBottonSheetGeneric(b: Boolean)
    fun closeDialog()
    fun statusCodeMotoboy(mobotoy: ResponseMotoboy, isResendMotoboy: Boolean = false)
    fun openBrowser(trackingUrl: String)
    fun delayCloseLocatesScreen(dialog: Dialog)
}