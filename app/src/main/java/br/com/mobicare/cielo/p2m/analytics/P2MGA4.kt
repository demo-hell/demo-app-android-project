package br.com.mobicare.cielo.p2m.analytics

import androidx.core.os.bundleOf
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import com.google.firebase.analytics.FirebaseAnalytics
import java.time.Clock.systemUTC

class P2MGA4 {

    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logException(screenName: String, error: NewErrorMessage? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to (error?.message.takeUnless { it.equals(DEFAULT_ERROR_MESSAGE) }  ?: error?.flagErrorCode.orEmpty()),
                Exception.STATUS_CODE to error?.httpCode.toString(),
            )
        )
    }

    fun logException(screenName: String, error: ErrorMessage? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to (error?.message.takeUnless { it.equals(DEFAULT_ERROR_MESSAGE) }  ?: error?.errorCode.orEmpty()),
                Exception.STATUS_CODE to error?.httpStatus.toString(),
            )
        )
    }

    fun logPurchase(isSelectedThirty: Boolean) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_P2M_SUCCESS,
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_ID to PURCHASE_SUCCESS_FORMAT.format(systemUTC().millis()),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                bundleOf(
                    FirebaseAnalytics.Param.ITEM_NAME to AUTOMATIC_RECEIVE,
                    FirebaseAnalytics.Param.ITEM_CATEGORY to SERVICES,
                    FirebaseAnalytics.Param.ITEM_CATEGORY2 to getPeriodCategoryName(isSelectedThirty),
                    ITEM_PROMOTION to OFFER_SELL_WHATSAPP
                )
            )
        )
    }

    private fun getPeriodCategoryName(isSelectedThirty: Boolean) =
        when(isSelectedThirty) {
            false -> TWO_DAYS
            true -> THIRTY_DAYS
        }

    companion object {
        private const val SCREEN_VIEW_P2M = "/home/p2m"
        const val SCREEN_VIEW_P2M_INTRODUCTION = "$SCREEN_VIEW_P2M/introducao"
        const val SCREEN_VIEW_P2M_CHOOSE = "$SCREEN_VIEW_P2M/escolha_o_prazo_de_recebimento"
        const val SCREEN_VIEW_P2M_TERM = "$SCREEN_VIEW_P2M/credenciamento/termo_de_aceite"
        const val SCREEN_VIEW_P2M_SUCCESS = "$SCREEN_VIEW_P2M/finalize_o_cadastro_no_whatsapp_business/sucesso"
        private const val AUTOMATIC_RECEIVE = "recebimento_automatico"
        private const val CONTRACTING = "contratacao"
        private const val SERVICES = "servicos"
        private const val TWO_DAYS = "2_dias"
        private const val THIRTY_DAYS = "30_dias"
        private const val PURCHASE_SUCCESS_FORMAT = "t_venda_whatsapp.%s"
        private const val ITEM_PROMOTION = "item_promotion"
        private const val OFFER_SELL_WHATSAPP = "oferta_venda_whatsapp"
    }
}