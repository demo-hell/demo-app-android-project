package br.com.mobicare.cielo.arv.analytics

import android.os.Bundle
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ANTICIPATIONS
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ARV
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ARV_AUTOMATIC_BASE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ARV_SINGLE_BASE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ARV_TRUNK
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.AUTOMATIC
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.BUTTON
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.CONTRACTING
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.MODAL_RANGE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_CANCEL_SUCCESS
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_SUCCESS
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SINGLE
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Cancel.CANCELLATION_REASON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase.CARD_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BRL
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SCREEN_VIEW_HOME
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_PROMOTION
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_CATEGORY
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_CATEGORY2
import com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.PROMOTION_NAME
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME

class ArvAnalyticsGA4 {
    fun logScreenView(
        screenName: String,
        pageNumber: String? = null,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(getScreenName(screenName, pageNumber))
    }

    private fun getScreenName(
        screenName: String,
        pageNumber: String?,
    ): String {
        return if (pageNumber?.isNotEmpty() == true) {
            "$screenName/$pageNumber"
        } else {
            screenName
        }
    }

    fun logViewPromotion(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = VIEW_PROMOTION,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenName,
                    CONTENT_TYPE to MODAL_RANGE,
                    PROMOTION_NAME to ARV_TRUNK,
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_NAME, ARV)
                    },
                ),
        )
    }

    fun logException(
        screenName: String,
        error: NewErrorMessage? = null,
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

    fun logAddPaymentInfo(
        screenName: String,
        bankName: String,
        itemCategory3: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenName,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                    PaymentAndPurchase.BANK to bankName.normalizeToLowerSnakeCase(),
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_CATEGORY, ANTICIPATIONS)
                        putString(ITEM_NAME, ARV)
                        putString(ITEM_CATEGORY2, AUTOMATIC)
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY3,
                            itemCategory3.normalizeToLowerSnakeCase(),
                        )
                    },
                ),
        )
    }

    fun logAnticipationSingleAddPaymentInfo(
        value: Double,
        periodStart: String? = null,
        periodEnd: String? = null,
        bankName: String,
        itemCategory3: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap =
                listOfNotNull(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_ARV_SINGLE_CONFIRMATION,
                    PaymentAndPurchase.CURRENCY to BRL,
                    PaymentAndPurchase.VALUE to value,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                    periodStart?.let { PaymentAndPurchase.PERIOD_START to periodStart },
                    periodEnd?.let { PaymentAndPurchase.PERIOD_END to periodEnd },
                    PaymentAndPurchase.BANK to bankName.normalizeToLowerSnakeCase(),
                ).toMap(),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_CATEGORY, ANTICIPATIONS)
                        putString(ITEM_NAME, ARV)
                        putDouble(FirebaseAnalytics.Param.PRICE, value)
                        putString(ITEM_CATEGORY2, SINGLE)
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY3,
                            itemCategory3.normalizeToLowerSnakeCase(),
                        )
                    },
                ),
        )
    }

    fun logSingleAnticipationSuccessPurchase(
        value: Double,
        transactionId: String,
        periodStart: String? = null,
        periodEnd: String? = null,
        bankName: String,
        itemCategory3: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap =
                listOfNotNull(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_ARV_SINGLE_SUCCESS,
                    PaymentAndPurchase.TRANSACTION_ID to transactionId,
                    PaymentAndPurchase.CURRENCY to BRL,
                    PaymentAndPurchase.VALUE to value,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                    periodStart?.let { PaymentAndPurchase.PERIOD_START to periodStart },
                    periodEnd?.let { PaymentAndPurchase.PERIOD_END to periodEnd },
                    PaymentAndPurchase.BANK to bankName.normalizeToLowerSnakeCase(),
                ).toMap(),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_CATEGORY, ANTICIPATIONS)
                        putString(ITEM_NAME, ARV)
                        putDouble(FirebaseAnalytics.Param.PRICE, value)
                        putString(ITEM_CATEGORY2, SINGLE)
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY3,
                            itemCategory3.normalizeToLowerSnakeCase(),
                        )
                    },
                ),
        )
    }

    fun logPurchase(
        screenName: String,
        transactionId: String,
        bankName: String,
        itemCategory3: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenName,
                    PaymentAndPurchase.TRANSACTION_ID to transactionId,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                    PaymentAndPurchase.BANK to bankName.normalizeToLowerSnakeCase(),
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_CATEGORY, ANTICIPATIONS)
                        putString(ITEM_NAME, ARV)
                        putString(ITEM_CATEGORY2, AUTOMATIC)
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY3,
                            itemCategory3.normalizeToLowerSnakeCase(),
                        )
                    },
                ),
        )
    }

    fun logDisplayContent(
        screenName: String,
        description: String,
        contentType: String? = null,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap =
                listOfNotNull(
                    ScreenView.SCREEN_NAME to screenName,
                    Exception.DESCRIPTION to description,
                    CONTENT_COMPONENT to MESSAGE,
                    contentType?.let { CONTENT_TYPE to contentType },
                ).toMap(),
        )
    }

    fun logClick(
        screenName: String,
        contentName: String,
        cardName: String? = null,
        contentComponent: String? = null,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap =
                listOfNotNull(
                    SCREEN_NAME to screenName,
                    CONTENT_TYPE to BUTTON,
                    cardName?.let { CARD_NAME to cardName },
                    CONTENT_NAME to contentName,
                    contentComponent?.let { CONTENT_COMPONENT to contentComponent },
                ).toMap(),
        )
    }

    fun logPromotionClick() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap =
                mapOf(
                    SCREEN_NAME to SCREEN_VIEW_HOME,
                    CONTENT_TYPE to MODAL_RANGE,
                    PROMOTION_NAME to ARV_TRUNK,
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_NAME, ARV)
                    },
                ),
        )
    }

    fun logBeginCheckout(
        screenName: String,
        itemCategory2: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenName,
                    PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING,
                ),
            eventsList =
                arrayListOf(
                    Bundle().apply {
                        putString(ITEM_CATEGORY, ANTICIPATIONS)
                        putString(ITEM_NAME, ARV)
                        putString(ITEM_CATEGORY2, itemCategory2)
                    },
                ),
        )
    }

    fun logCancel(reason: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Cancel.CANCEL_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_ARV_CANCEL_SUCCESS,
                    CANCELLATION_REASON to reason.normalizeToLowerSnakeCase(),
                ),
        )
    }

    companion object {
        val tArvAutomatic: String
            get() = ARV_AUTOMATIC_BASE + System.currentTimeMillis()
        val tArvSingle: String
            get() = ARV_SINGLE_BASE + System.currentTimeMillis()
    }
}
