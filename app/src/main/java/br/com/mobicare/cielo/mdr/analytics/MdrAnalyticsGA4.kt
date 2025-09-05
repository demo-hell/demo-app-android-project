package br.com.mobicare.cielo.mdr.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase.ITEM_PROMOTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.CONTRACTING
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.MDR
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.MDR_OFFER_BASE
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME
import br.com.mobicare.cielo.mdr.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_MDR_HOME_CONTRACTING_SUCCESS
import com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_PROMOTION
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.PROMOTION_ID
import com.google.firebase.analytics.FirebaseAnalytics.Param.PROMOTION_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME

class MdrAnalyticsGA4 {
    fun logViewPromotion(
        promotionName: String,
        promotionId: Int,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = VIEW_PROMOTION,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_MDR_HOME,
                    CONTENT_TYPE to MODAL,
                    PROMOTION_NAME to promotionName.normalizeToLowerSnakeCase(),
                    PROMOTION_ID to promotionId.toString(),
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_NAME, MDR)
                    },
                ),
        )
    }

    fun logException(
        screenName: String,
        error: NewErrorMessage?,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenName,
                    Exception.DESCRIPTION to
                        (
                            error?.message.takeUnless {
                                it.equals(DEFAULT_ERROR_MESSAGE)
                            } ?: error?.flagErrorCode.orEmpty()
                        ).normalizeToLowerSnakeCase(),
                    Exception.STATUS_CODE to error?.httpCode?.toString().orEmpty(),
                ),
        )
    }

    fun logPurchase(
        transactionId: String,
        promotionName: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_MDR_HOME_CONTRACTING_SUCCESS,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                    PaymentAndPurchase.TRANSACTION_ID to transactionId,
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_NAME, MDR)
                        putString(ITEM_PROMOTION, promotionName.normalizeToLowerSnakeCase())
                    },
                ),
        )
    }

    fun logClick(
        screenName: String,
        contentName: String,
        promotionName: String,
        promotionId: Int,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap =
                mapOf(
                    SCREEN_NAME to screenName,
                    CONTENT_TYPE to MODAL,
                    CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                    PROMOTION_NAME to promotionName.normalizeToLowerSnakeCase(),
                    PROMOTION_ID to promotionId.toString(),
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_NAME, MDR)
                    },
                ),
        )
    }

    companion object {
        val tMdrOfferTimeStamp: String
            get() = MDR_OFFER_BASE + System.currentTimeMillis()
    }
}
