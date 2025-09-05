package br.com.mobicare.cielo.pixMVVM.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.HIRING
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.AUTHORIZATION
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.OTHERS
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.PIX
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.PIX_ADHERENCE_TRANSACTION_ID
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.PIX_TRANSACTION_ID
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.SALE
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.SERVICES
import com.google.firebase.analytics.FirebaseAnalytics

object PixAnalytics {

    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logAdherencePurchase() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to ScreenView.ADHERENCE_REQUEST_SUCCESS,
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_ID to String.format(
                    PIX_ADHERENCE_TRANSACTION_ID, getTimestampNow()
                ),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to HIRING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, PIX)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                }
            )
        )
    }

    fun logTransactionPurchase(amount: Double, transactionType: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to ScreenView.TRANSFER_SUCCESS,
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_ID to String.format(
                    PIX_TRANSACTION_ID, getTimestampNow()
                ),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to SALE,
                GoogleAnalytics4Events.PaymentAndPurchase.VALUE to amount
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, PIX)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                    putString(FirebaseAnalytics.Param.PRICE, amount.toString())
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, transactionType.normalizeToLowerSnakeCase())
                }
            )
        )
    }

    fun logException(screenView: String, statusCode: String? = null, description: String? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap = mutableMapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenView
            ).also {
                if (statusCode != null)
                    it[GoogleAnalytics4Events.Exception.STATUS_CODE] = statusCode
                if (description != null)
                    it[GoogleAnalytics4Events.Exception.DESCRIPTION] = description.normalizeToLowerSnakeCase()
            }
        )
    }

    object Values {
        const val PIX = "pix"
        const val SERVICES = "servicos"
        const val ADHERENCE_REQUEST = "solicitar_adesao"
        const val SUCCESS = "sucesso"
        const val HIRING = "contratacao"
        const val PIX_ADHERENCE_TRANSACTION_ID = "t_adesao_pix.%s"
        const val PIX_TRANSACTION_ID = "t_pix.%s"
        const val TRANSFER = "transferir"
        const val SALE = "venda"
        const val OTHERS = "outros"
        const val AUTHORIZATION = "autorizacoes"
    }

    object ScreenView {
        private const val SERVICES_PIX = "$SERVICES/$PIX"
        const val ADHERENCE_REQUEST = "/$SERVICES_PIX/${Values.ADHERENCE_REQUEST}"
        const val ADHERENCE_REQUEST_SUCCESS = "/$SERVICES_PIX/${Values.ADHERENCE_REQUEST}/${Values.SUCCESS}"
        const val TRANSFER = "/$SERVICES_PIX/${Values.TRANSFER}"
        const val TRANSFER_SUCCESS = "/$SERVICES_PIX/${Values.TRANSFER}/${Values.SUCCESS}"
        const val PIX_AUTHORIZATION = "/$OTHERS/$AUTHORIZATION/$PIX"
    }

}