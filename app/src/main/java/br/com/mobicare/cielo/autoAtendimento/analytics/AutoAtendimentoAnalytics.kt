package br.com.mobicare.cielo.autoAtendimento.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import com.google.firebase.analytics.FirebaseAnalytics

class AutoAtendimentoAnalytics {

    fun logScreenView(screenName: String = SCREEN_VIEW_REQUEST_MACHINE) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logException(screenName: String, errorCode: String, errorMessage: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to errorMessage,
                Exception.STATUS_CODE to errorCode,
            )
        )
    }

    fun logRequestMachineBeginCheckout(machineName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MACHINE,
                PaymentAndPurchase.TRANSACTION_TYPE to RENTAL
            ),
            eventsList = getRequestMachineEventsList(machineName)
        )
    }

    fun logRequestMachineAddPaymentInfo(machineName: String, value: Double, quantity: Int) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MACHINE_QUANTITY,
                PaymentAndPurchase.CURRENCY to GoogleAnalytics4Values.BRL,
                PaymentAndPurchase.VALUE to value,
                PaymentAndPurchase.TRANSACTION_TYPE to RENTAL
            ),
            eventsList = getRequestMachineEventsList(machineName, quantity)
        )
    }

    fun logRequestMachinePurchase(machineName: String, quantity: Int, value: Double) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MACHINE_SUCCESS,
                PaymentAndPurchase.CURRENCY to GoogleAnalytics4Values.BRL,
                PaymentAndPurchase.VALUE to value,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    MACHINE_REQUEST_TRANSACTION_ID, getTimestampNow()
                ),
                PaymentAndPurchase.TRANSACTION_TYPE to RENTAL
            ),
            eventsList = getRequestMachineEventsList(machineName, quantity)
        )
    }

    private fun getRequestMachineEventsList(machineName: String, quantity: Int? = null) =
        arrayListOf(
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, machineName.normalizeToLowerSnakeCase())
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, RENTAL_MACHINE)
                quantity?.let { putString(FirebaseAnalytics.Param.QUANTITY, it.toString()) }
            }
        )

    companion object {
        const val SERVICES = "servicos"
        const val RENTAL = "aluguel"
        const val RENTAL_MACHINE = "aluguel_maquininha"
        const val MACHINE_REQUEST_TRANSACTION_ID = "t_aluguel_maquininha.%s"

        const val SCREEN_VIEW_REQUEST_MACHINE = "/servicos/solicitar_maquininha"
        const val SCREEN_VIEW_REQUEST_MACHINE_QUANTITY = "${SCREEN_VIEW_REQUEST_MACHINE}/quantidade"
        const val SCREEN_VIEW_REQUEST_MACHINE_ADDRESS = "${SCREEN_VIEW_REQUEST_MACHINE}/endereco"
        const val SCREEN_VIEW_REQUEST_MACHINE_CONTACT = "${SCREEN_VIEW_REQUEST_MACHINE}/contato"
        const val SCREEN_VIEW_REQUEST_MACHINE_PERIOD = "${SCREEN_VIEW_REQUEST_MACHINE}/periodo"
        const val SCREEN_VIEW_REQUEST_MACHINE_CONFIRM_DATA = "${SCREEN_VIEW_REQUEST_MACHINE}/confirmar_dados"
        const val SCREEN_VIEW_REQUEST_MACHINE_SUCCESS = "${SCREEN_VIEW_REQUEST_MACHINE}/sucesso"
    }

}