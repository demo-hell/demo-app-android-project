package br.com.mobicare.cielo.tapOnPhone.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Share
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.analytics.FirebaseAnalytics
import java.math.BigDecimal
import java.math.RoundingMode

class TapOnPhoneGA4 {

    fun logScreenView(screenName: String) {
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

    fun logClick(
        screenName: String,
        contentName: String,
        contentType: String = GoogleAnalytics4Values.BUTTON,
        contentComponent: String? = null
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
            ).also { map ->
                contentComponent?.let {
                    map[Navigation.CONTENT_COMPONENT] = it.normalizeToLowerSnakeCase()
                }
            }
        )
    }

    fun logOnBoardingStepScreenView(pageIndex: Int, title: String) {
        logScreenView(getOnBoardingStepScreenView(pageIndex, title))
    }

    fun logOnBoardingBeginCheckout(pageIndex: Int, title: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to getOnBoardingStepScreenView(pageIndex, title),
                PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CIELO_TAP)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, POS_VIRTUAL)
                }
            )
        )
    }

    fun logAccreditationAddPaymentInfo(screenName: String, bankName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                PaymentAndPurchase.BANK to bankName.normalizeToLowerSnakeCase(),
                PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CIELO_TAP)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, POS_VIRTUAL)
                }
            )
        )
    }

    fun logPurchase(screenName: String, transactionId: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                PaymentAndPurchase.TRANSACTION_ID to transactionId,
                PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CIELO_TAP)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, POS_VIRTUAL)
                }
            )
        )
    }

    fun logSaleValueBeginCheckout(value: BigDecimal) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_TRANSACTIONAL_SALE_VALUE,
                PaymentAndPurchase.CURRENCY to BRL,
                PaymentAndPurchase.VALUE to value.toString(),
                PaymentAndPurchase.TRANSACTION_TYPE to SALE
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CIELO_TAP)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, POS_VIRTUAL)
                }
            )
        )
    }

    fun logSaleValueAddPaymentInfo(value: BigDecimal, paymentType: String, installment: Int? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_TRANSACTIONAL_MAKE_SALE,
                PaymentAndPurchase.CURRENCY to BRL,
                PaymentAndPurchase.VALUE to value.toString(),
                PaymentAndPurchase.PAYMENT_TYPE to paymentType.normalizeToLowerSnakeCase(),
                PaymentAndPurchase.TRANSACTION_TYPE to SALE
            ).also { map ->
                installment?.let { installment ->
                    map[PaymentAndPurchase.INSTALLMENT] = installment.toString()
                    map[PaymentAndPurchase.INSTALLMENT_VALUE] =
                        (value / installment.toBigDecimal())
                            .setScale(TWO, RoundingMode.HALF_EVEN)
                            .toString()
                }
            },
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CIELO_TAP)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, POS_VIRTUAL)
                }
            )
        )
    }

    fun logSaleNotMadeScreenView(paymentMethod: String) {
        logScreenView(getTransactionSaleNotMadeScreenView(paymentMethod))
    }

    fun logTransactionErrorScreenView(
        paymentMethod: String,
        errorMessage: String = GENERIC_ERROR,
        errorCode: String = EMPTY
    ) {
        logScreenView(SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_ERROR
            .format(paymentMethod, errorMessage.normalizeToLowerSnakeCase(), errorCode))
    }

    fun logTransactionErrorException(
        paymentMethod: String,
        errorMessage: String = GENERIC_ERROR,
        errorCode: String = EMPTY
    ) {
        logException(
            screenName = SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_ERROR.format(paymentMethod, errorMessage, errorCode),
            errorMessage = errorMessage,
            errorCode = errorCode
        )
    }

    fun logPaymentReceiptScreenView(paymentMethod: String) {
        logScreenView(getTransactionalReceiptScreenView(paymentMethod))
    }

    fun logPaymentReceiptShare(paymentMethod: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Share.SHARE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to getTransactionalReceiptScreenView(paymentMethod),
                Navigation.CONTENT_TYPE to IMAGE,
                Navigation.CONTENT_COMPONENT to RECEIPT,
            )
        )
    }

    private fun getOnBoardingStepScreenView(pageIndex: Int, title: String) =
        SCREEN_VIEW_ONBOARDING_STEPS.format(pageIndex + ONE, title.normalizeToLowerSnakeCase())

    companion object {
        private const val POS_VIRTUAL = "pos_virtual"
        private const val CIELO_TAP = "cielo_tap"
        private const val TRANSACTIONAL = "transacional"
        private const val ERROR_WARN = "aviso_de_problema"
        private const val STATUS = "status"
        private const val DEACTIVATED_ON_YOUR_ACCOUNT = "desativado_na_sua_conta"
        private const val CONTRACTING = "contratacao"
        private const val ACCREDITATION = "credenciamento"
        private const val OFFER_LOAD = "carregamento_oferta"
        private const val OFFER = "oferta"
        private const val FEES_BY_INSTALLMENT = "taxas_por_parcelas"
        private const val CANNOT_PROCEED_WITH_ACCREDITATION = "nao_podemos_prosseguir_no_credenciamento"
        private const val IMPERSONATE = "impersonate"
        private const val SALE_VALUE = "valor_da_venda"
        private const val BRL = "BRL"
        private const val TRANSACTION_CIELO_TAP = "transacao_cielo_tap"
        private const val SALE = "venda"
        private const val MAKE_SALE = "realizar_venda"
        private const val GENERIC_ERROR = "erro_generico"
        private const val RECEIPT = "comprovante"
        private const val IMAGE = "imagem"
        const val TERM_AND_CONDITIONS = "termos_e_condicoes_de_uso"
        const val CALL_TO_CALL_CENTER = "ligar_para_central"
        const val CONFIRM = "confirmar"
        const val YOUR_PHONE_IS_NOT_COMPATIBLE = "seu_celular_nao_e_compativel"
        const val CHANGE_TO_CIELO_TAP = "trocar_para_cielo_tap"
        const val DEBIT = "debito"
        const val CREDIT = "credito_a_vista"
        const val INSTALLMENT_CREDIT = "credito_parcelado_loja"
        const val NEW_SALE = "nova_venda"
        const val CANCELED_SALE = "venda_cancelada"
        const val EXPIRED_TIME = "tempo_expirado"

        private const val SCREEN_VIEW_CIELO_TAP = "$POS_VIRTUAL/$CIELO_TAP"
        const val SCREEN_VIEW_API_STATUS_ERROR = "/$SCREEN_VIEW_CIELO_TAP/$STATUS/$ERROR_WARN"
        const val SCREEN_VIEW_DEACTIVATED = "/$SCREEN_VIEW_CIELO_TAP/$DEACTIVATED_ON_YOUR_ACCOUNT"
        const val SCREEN_VIEW_ONBOARDING_STEPS = "/$SCREEN_VIEW_CIELO_TAP/onboarding%d/%s"
        const val SCREEN_VIEW_ACCREDITATION_OFFER_LOAD = "/$SCREEN_VIEW_CIELO_TAP/$ACCREDITATION/$OFFER_LOAD"
        const val SCREEN_VIEW_OFFER = "/$SCREEN_VIEW_CIELO_TAP/$OFFER"
        const val SCREEN_VIEW_FEES_BY_INSTALLMENT = "/$SCREEN_VIEW_CIELO_TAP/$FEES_BY_INSTALLMENT"
        const val SCREEN_VIEW_TERM_AND_CONDITIONS = "/$SCREEN_VIEW_CIELO_TAP/$TERM_AND_CONDITIONS"
        const val SCREEN_VIEW_ACCREDITATION_CANNOT_PROCEED = "/$SCREEN_VIEW_CIELO_TAP/$CANNOT_PROCEED_WITH_ACCREDITATION"
        const val SCREEN_VIEW_ACCREDITATION_TERM_ACCEPT_ERROR = "/$SCREEN_VIEW_CIELO_TAP/$ACCREDITATION/aceite_do_termo/$ERROR_WARN"
        const val SCREEN_VIEW_ACCREDITATION_ORDER_SUCCESS = "/$SCREEN_VIEW_CIELO_TAP/solicitacao_para_habilitar_o_cielo_tap_realizada_pedido"
        const val SCREEN_VIEW_TRANSACTIONAL_SDK_INITIALIZE_ERROR = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/$ERROR_WARN"
        const val SCREEN_VIEW_TRANSACTIONAL_IMPERSONATE = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/$IMPERSONATE"
        const val SCREEN_VIEW_TRANSACTIONAL_SALE_VALUE = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/$SALE_VALUE"
        const val SCREEN_VIEW_TRANSACTIONAL_MAKE_SALE = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/$MAKE_SALE"
        const val SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_ERROR = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/%s/%s/%s"
        private const val SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_SALE_NOT_MADE = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/%s/venda_nao_realizada"
        private const val SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_RECEIPT = "/$SCREEN_VIEW_CIELO_TAP/$TRANSACTIONAL/%s/comprovante"

        fun getTransactionSaleNotMadeScreenView(paymentMethod: String) =
            SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_SALE_NOT_MADE.format(paymentMethod)

        fun getTransactionalReceiptScreenView(paymentMethod: String) =
            SCREEN_VIEW_TRANSACTIONAL_PAYMENT_FLOW_RECEIPT.format(paymentMethod)
    }

}