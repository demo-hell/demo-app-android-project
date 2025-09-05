package br.com.mobicare.cielo.posVirtual.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Share
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BRL
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.REFERENCE_CODE_AUTOMATIC_RECEIPT
import com.google.firebase.analytics.FirebaseAnalytics.Param
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class PosVirtualAnalytics {

    fun logScreenView(screenPath: String) = ga4.trackScreenView(screenPath)

    fun logBeginCheckoutOnboarding(screenPath: String) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                PaymentAndPurchase.TRANSACTION_TYPE to HIRING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_CATEGORY, MAIN_SERVICES)
                    putString(Param.ITEM_NAME, POS_VIRTUAL)
                }
            )
        )
    }

    fun logException(screenPath: String, error: NewErrorMessage?) {
        val description = error?.let {
            it.message.ifEmpty { it.flagErrorCode }
        } ?: EMPTY

        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to error?.httpCode.toStringOrEmpty()
            )
        )
    }

    fun logAddPaymentInfoHire(products: List<String>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_ACCREDITATION,
                PaymentAndPurchase.TRANSACTION_TYPE to HIRING
            ),
            eventsList = generateArrayListContractedProducts(products)
        )
    }

    fun logClick(screenPath: String, contentType: String, contentName: String) {
        ga4.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logClick(
        screenPath: String,
        contentComponent: String,
        contentType: String,
        contentName: String
    ) {
        ga4.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_COMPONENT to contentComponent.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logPurchaseSuccessHirePOS(bank: String, products: List<String>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_ACCREDITATION_SUCCESS_ACCEPT_OFFER,
                PaymentAndPurchase.TRANSACTION_TYPE to HIRING,
                PaymentAndPurchase.BANK to bank.normalizeToLowerSnakeCase()
            ),
            eventsList = generateArrayListContractedProducts(products)
        )
    }

    fun logDisplayContentAccreditationBankingDomicile(
        description: String,
        contentComponent: String? = null
    ) {
        val eventsMap = mutableMapOf(
            ScreenView.SCREEN_NAME to SCREEN_VIEW_ACCREDITATION_BANKING_DOMICILE,
            Navigation.CONTENT_TYPE to MODAL,
            Exception.DESCRIPTION to description
        )
        contentComponent?.let { eventsMap.put(Navigation.CONTENT_COMPONENT, it) }

        ga4.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = eventsMap
        )
    }

    private fun generateArrayListContractedProducts(products: List<String>): ArrayList<Bundle> {
        return ArrayList(
            products.map { product ->
                Bundle().apply {
                    if (product != REFERENCE_CODE_AUTOMATIC_RECEIPT) {
                        putString(Param.ITEM_CATEGORY, MAIN_SERVICES)
                        putString(Param.ITEM_NAME, POS_VIRTUAL)
                        putString(Param.ITEM_CATEGORY2, parseReferenceProductToNameProduct(product))
                    } else {
                        putString(Param.ITEM_CATEGORY, SERVICES)
                        putString(Param.ITEM_NAME, PRODUCT_AUTOMATIC_RECEIPT)
                        putString(Param.ITEM_CATEGORY2, PRODUCT_AUTOMATIC_RECEIPT_POS_VIRTUAL)
                        putString(Param.ITEM_CATEGORY3, PRODUCT_AUTOMATIC_RECEIPT_DEADLINE)
                        putString(PaymentAndPurchase.ITEM_PROMOTION, OFFER_POS_VIRTUAL)
                    }
                }
            }
        )
    }

    private fun parseReferenceProductToNameProduct(reference: String): String {
        return when (reference) {
            PosVirtualConstants.REFERENCE_CODE_QR_CODE_PIX -> PRODUCT_QR_CODE_PIX
            PosVirtualConstants.REFERENCE_CODE_CIELO_TAP -> PRODUCT_CIELO_TAP
            PosVirtualConstants.REFERENCE_CODE_SUPER_LINK -> PRODUCT_PAYMENT_LINK
            else -> EMPTY
        }
    }

    fun logSelectContentHome(productName: String, productStatus: String) {
        ga4.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_COMPONENT to productStatus.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_NAME to productName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logBeginCheckoutInsertValueQRCodePix() {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_INSERT_VALUE,
                PaymentAndPurchase.CURRENCY to BRL,
                PaymentAndPurchase.TRANSACTION_TYPE to SALE
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_CATEGORY, MAIN_SERVICES)
                    putString(Param.ITEM_NAME, QRCODE_PIX)
                }
            )
        )
    }

    fun logDisplayContentQRCodeInsertValue() {
        ga4.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_INSERT_VALUE,
                Navigation.CONTENT_TYPE to MODAL,
                Exception.DESCRIPTION to TITLE_BS_ERROR_LIMIT_EXCEEDED
            )
        )
    }

    fun logPurchaseGenerateQRCodePix(value: Double) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    TRANSACTION_ID_GENERATE_QRCODE_PIX, getTimestampNow()
                ),
                PaymentAndPurchase.CURRENCY to BRL,
                PaymentAndPurchase.VALUE to value,
                PaymentAndPurchase.TRANSACTION_TYPE to SALE
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_CATEGORY, MAIN_SERVICES)
                    putString(Param.ITEM_NAME, QRCODE_PIX)
                    putDouble(PaymentAndPurchase.PRICE, value)
                }
            )
        )
    }

    fun logShareQRCodePixFromCopyCode() {
        ga4.trackEvent(
            eventName = Share.SHARE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE,
                Navigation.CONTENT_TYPE to SHARE_QRCODE,
                Navigation.CONTENT_COMPONENT to CONTENT_COMPONENT_COPY_CODE
            )
        )
    }

    fun logClickShareButton() {
        ga4.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_COMPONENT to CONTENT_COMPONENT_CLICK_SHARE_BUTTON,
                Navigation.CONTENT_NAME to CONTENT_NAME_CLICK_SHARE_BUTTON,
            )
        )
    }

    fun logShareQRCodePixFromShareButton(contentType: String) {
        ga4.trackEvent(
            eventName = Share.SHARE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_COMPONENT to QRCODE_PIX
            )
        )
    }

    fun logExceptionLoadQRCodePix() {
        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE,
                Exception.DESCRIPTION to EXCEPTION_LOAD_QR_CODE_PIX
            )
        )
    }

    companion object {

        private const val MAIN_SERVICES = "principais_servicos"
        private const val SERVICES = "servicos"
        private const val POS_VIRTUAL = "pos_virtual"
        private const val HIRING = "contratacao"
        private const val ACCREDITATION = "credenciamento"
        private const val SALE = "venda"
        private const val MODAL = "modal"
        private const val TITLE_BS_ERROR_LIMIT_EXCEEDED = "valor_acima_do_limite_qr_code"
        private const val TRANSACTION_ID_GENERATE_QRCODE_PIX = "pos_virtual_qr_code_pix.%s"
        private const val CONTENT_COMPONENT_CLICK_SHARE_BUTTON = "mostre_ou_compartilhe_o_qr_code"
        private const val CONTENT_NAME_CLICK_SHARE_BUTTON = "compartilhar"
        private const val PRODUCT_AUTOMATIC_RECEIPT_POS_VIRTUAL = "recebimento_automatico_pos"
        private const val PRODUCT_AUTOMATIC_RECEIPT_DEADLINE = "2_dias"
        private const val OFFER_POS_VIRTUAL = "oferta_pos_virtual"
        private const val EXCEPTION_LOAD_QR_CODE_PIX = "nao_foi_possivel_carregar_qr_code"
        const val DESCRIPTION_SELECT_BANK = "selecione_conta_bancaria"
        const val DESCRIPTION_TERMS_AND_CONDITIONS = "termos_e_condicoes"

        const val QRCODE_PIX = "qr_code_pix"
        const val PRODUCT_CIELO_TAP = "cielo_tap"
        const val PRODUCT_QR_CODE_PIX = "qr_code_pix"
        const val PRODUCT_AUTOMATIC_RECEIPT = "recebimento_automatico"
        const val PRODUCT_PAYMENT_LINK = "link_de_pagamento"
        const val CONTENT_COMPONENT_BOTTOM_SHEET_GENERIC_TITLE = "parece_que_tivemos_um_problema"
        const val CONTENT_COMPONENT_BANK = "domicilio_bancario"
        const val TERMS_AND_CONDITIONS = "termos_e_condicoes"
        const val CONTENT_COMPONENT_RATES_DETAILS_OPEN_FAQ = "taxas_%s"
        const val CONFIRM_HIRE = "confirme_os_prazos"
        const val CONTENT_COMPONENT_COPY_CODE = "copiar_codigo"
        const val SHARE_QRCODE = "qr_code"
        const val SHARE_IMAGE = "imagem"
        const val SHARE_CODE = "codigo"

        const val SCREEN_VIEW_ONBOARDING_STEPS = "/$POS_VIRTUAL/onboarding/%s"
        const val SCREEN_VIEW_ACCREDITATION = "/$POS_VIRTUAL/$ACCREDITATION"
        const val SCREEN_VIEW_ACCREDITATION_RATES = "/$POS_VIRTUAL/$ACCREDITATION/taxas/%s"
        const val SCREEN_VIEW_ACCREDITATION_BANKING_DOMICILE =
            "/$POS_VIRTUAL/$ACCREDITATION/domicilio_bancario"
        const val SCREEN_VIEW_ACCREDITATION_SUCCESS_ACCEPT_OFFER =
            "/$POS_VIRTUAL/$ACCREDITATION/sucesso/aceite_oferta"
        const val SCREEN_VIEW_HOME = "/$POS_VIRTUAL/como_voce_deseja_vender_hoje"
        const val SCREEN_VIEW_REQUEST_DETAILS = "/$POS_VIRTUAL/detalhes_da_solicitacao/%s"
        const val SCREEN_VIEW_QRCODE_PIX_INSERT_VALUE =
            "/$POS_VIRTUAL/$QRCODE_PIX/valor_da_venda"
        const val SCREEN_VIEW_QRCODE_PIX_SUCCESS_GENERATE_QRCODE =
            "/$POS_VIRTUAL/$QRCODE_PIX/qr_code"

    }

}