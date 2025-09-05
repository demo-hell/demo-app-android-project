package br.com.mobicare.cielo.superlink.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Share
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics.Values.SALE
import com.google.firebase.analytics.FirebaseAnalytics.Param
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class PaymentLinkGA4 {

    fun logScreenView(screenPath: String) = ga4.trackScreenView(screenPath)

    fun logException(screenPath: String, error: ErrorMessage?) {
        val description = error?.let {
            it.message.ifEmpty { it.code }
        } ?: Text.EMPTY

        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                DESCRIPTION to description.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to error?.httpStatus.toStringOrEmpty()
            )
        )
    }

    fun logClickButtonHome(labelButton: String, contentComponent: String) {
        ga4.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_PAYMENT_LINK,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to labelButton.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_COMPONENT to contentComponent.normalizeToLowerSnakeCase(),
            )
        )
    }

    fun logSelectContentSelectSaleType(labelButton: String) {
        ga4.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_PAYMENT_LINK_SELECT_SALE_TYPE,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to labelButton.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_COMPONENT to PURPOSE_PAYMENT_LINK,
            )
        )
    }

    fun logPurchaseCreatedLink(screenPath: String, amount: Double) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                PaymentAndPurchase.TRANSACTION_TYPE to SALE,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    TRANSACTION_ID_PAYMENT_LINK, getTimestampNow()
                ),
                PaymentAndPurchase.CURRENCY to GoogleAnalytics4Values.BRL,
                PaymentAndPurchase.VALUE to amount,
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_CATEGORY, PAYMENT_LINK)
                    putString(Param.ITEM_NAME, OTHERS)
                    putDouble(PaymentAndPurchase.PRICE, amount)
                }
            )
        )
    }

    fun logClickDeleteLink() {
        ga4.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_PAYMENT_LINK_DETAILS,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to DELETE_LINK,
            )
        )
    }

    fun logShareLink() {
        ga4.trackEvent(
            eventName = Share.SHARE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_PAYMENT_LINK_DETAILS,
                Navigation.CONTENT_NAME to SHARE_LINK,
            )
        )
    }

    fun logDisplayContentLinkDetails() {
        ga4.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_PAYMENT_LINK_DETAILS,
                DESCRIPTION to LINK_OF_DETAILS,
                Navigation.CONTENT_TYPE to MODAL,
            )
        )
    }

    companion object {

        private const val CHARGE_AMOUNT = "cobrar_valor"
        private const val DELETE_LINK = "deletar_link"
        private const val LINK_DETAILS = "detalhes_link"
        private const val LINK_OF_DETAILS = "detalhes_do_link"
        private const val OTHERS = "outros"
        private const val PAYMENT_LINK = "link_de_pagamento"
        private const val POS_VIRTUAL = "pos_virtual"
        private const val PURPOSE_PAYMENT_LINK = "finalidade_link_de_pagamento"
        private const val RECURRENT = "recorrente"
        private const val SALE_DETAILS = "detalhes_venda"
        private const val SALE_TYPE = "tipo_de_venda"
        private const val SEND_PRODUCT = "enviar_um_produto"
        private const val SHARE_LINK = "compartilhar_link"
        private const val SHIPPING_METHOD = "forma_de_envio"
        private const val TO_CONSULT = "consultar"
        private const val TRANSACTION_ID_PAYMENT_LINK = "t_vendas_link_de_pagamento.%s"

        const val CLIENT_SCREEN = "tela_cliente"
        const val CODE = "codigo"
        const val LAST_ACTIVE_LINKS = "ultimos_links_ativos"
        const val LINK_GENERATE = "link_gerado"
        const val NEW_PAYMENT = "novo_pagamento"
        const val SEE_ALL_GENERATED_LINKS = "ver_todos_links_gerados"
        const val SKU = "sku"
        const val VALUE = "valor"

        const val SCREEN_VIEW_PAYMENT_LINK = "/$OTHERS/$POS_VIRTUAL/$PAYMENT_LINK"
        const val SCREEN_VIEW_PAYMENT_LINK_SELECT_SALE_TYPE = "$SCREEN_VIEW_PAYMENT_LINK/$SALE_TYPE"
        const val SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT =
            "$SCREEN_VIEW_PAYMENT_LINK/$SEND_PRODUCT/"
        const val SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT_SELECT_SHIPPING_METHOD =
            "$SCREEN_VIEW_PAYMENT_LINK/$SEND_PRODUCT/$SHIPPING_METHOD"
        const val SCREEN_VIEW_PAYMENT_LINK_CHARGE_AMOUNT =
            "$SCREEN_VIEW_PAYMENT_LINK/$CHARGE_AMOUNT/"
        const val SCREEN_VIEW_PAYMENT_SALE_DETAILS =
            "$SCREEN_VIEW_PAYMENT_LINK/$TO_CONSULT/$SALE_DETAILS"
        const val SCREEN_VIEW_PAYMENT_LINK_RECURRENT = "$SCREEN_VIEW_PAYMENT_LINK/$RECURRENT/"
        const val SCREEN_VIEW_PAYMENT_LINK_DETAILS =
            "$SCREEN_VIEW_PAYMENT_LINK/$TO_CONSULT/$LINK_DETAILS"

    }

}