package br.com.mobicare.cielo.autoAtendimento.analytics

import android.os.Bundle
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.PaymentAndPurchase
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import com.google.firebase.analytics.FirebaseAnalytics.Param
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class SelfServiceAnalytics {

    fun logScreenView(screenPath: String) = ga4.trackScreenView(screenPath)

    fun logSelectContentMaterial(material: String) {
        ga4.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS,
                Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                Navigation.CONTENT_COMPONENT to REQUEST_MATERIALS,
                Navigation.CONTENT_NAME to material.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logBeginCheckoutCoil(coilName: String) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_COIL,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_NAME, coilName.normalizeToLowerSnakeCase())
                    putString(Param.ITEM_CATEGORY, SERVICES)
                    putString(Param.ITEM_CATEGORY2, REQUEST_COILS)
                }
            )
        )
    }

    fun logPurchaseCoil(coilName: String) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_COIL_SUCCESS_REQUEST,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    TRANSACTION_ID_REQUEST_COILS, getTimestampNow()
                ),
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(Param.ITEM_NAME, coilName.normalizeToLowerSnakeCase())
                    putString(Param.ITEM_CATEGORY, SERVICES)
                    putString(Param.ITEM_CATEGORY2, COILS)
                }
            )
        )
    }

    fun logBeginCheckoutFilm(films: ArrayList<CoilOptionObj>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_FILM,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS
            ),
            eventsList = generateItems(films, FILMS)
        )
    }

    fun logPurchaseFilm(films: ArrayList<CoilOptionObj>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_FILM_SUCCESS_REQUEST,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    TRANSACTION_ID_REQUEST_FILMS, getTimestampNow()
                ),
            ),
            eventsList = generateItems(films, FILMS)
        )
    }

    fun logBeginCheckoutSticker(stickers: ArrayList<CoilOptionObj>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_STICKER,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS
            ),
            eventsList = generateItems(stickers, STICKERS)
        )
    }

    fun logPurchaseSticker(stickers: ArrayList<CoilOptionObj>) {
        ga4.trackEvent(
            eventName = PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_REQUEST_MATERIALS_STICKER_SUCCESS_REQUEST,
                PaymentAndPurchase.TRANSACTION_TYPE to REQUEST_MATERIALS,
                PaymentAndPurchase.TRANSACTION_ID to String.format(
                    TRANSACTION_ID_REQUEST_STICKERS, getTimestampNow()
                ),
            ),
            eventsList = generateItems(stickers, STICKERS)
        )
    }

    fun logException(screenPath: String, error: ErrorMessage?) {
        val description = error?.let {
            it.message.ifEmpty { it.errorCode }
        } ?: Text.EMPTY

        ga4.trackEvent(
            eventName = GoogleAnalytics4Events.Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenPath,
                GoogleAnalytics4Events.Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                GoogleAnalytics4Events.Exception.STATUS_CODE to error?.httpStatus.toStringOrEmpty()
            )
        )
    }

    private fun generateItems(items: ArrayList<CoilOptionObj>, category: String): ArrayList<Bundle> {
        return items.map { item ->
            Bundle().apply {
                putString(Param.ITEM_NAME, item.title.normalizeToLowerSnakeCase())
                putString(Param.ITEM_CATEGORY, SERVICES)
                putString(Param.ITEM_CATEGORY2, category)
                putInt(Param.QUANTITY, item.quantity)
            }
        }.toCollection(ArrayList())
    }

    companion object {

        private const val SERVICES = "servicos"
        private const val REQUEST_MATERIALS = "solicitar_materiais"
        private const val REQUEST_COILS = "solicitar_bobinas"
        private const val COIL = "bobina"
        private const val CONFIRM_QUANTITY = "confirmar_quantidade"
        private const val CONFIRM_REQUEST = "confirmar_solicitacao"
        private const val SUCCESS = "sucesso"
        private const val REQUEST_IN_PROGRESS = "solicitacao_em_andamento"
        private const val TRANSACTION_ID_REQUEST_COILS = "t_solicitar_bobinas.%s"
        private const val TRANSACTION_ID_REQUEST_FILMS = "t_solicitar_peliculas.%s"
        private const val TRANSACTION_ID_REQUEST_STICKERS = "t_solicitar_adesivos.%s"

        const val TO_ADD = "adicionar"
        const val TO_REMOVE = "remover"
        const val STICKERS = "adesivos"
        const val COILS = "bobinas"
        const val FILMS = "peliculas"

        const val SCREEN_VIEW_REQUEST_MATERIALS = "/$SERVICES/$REQUEST_MATERIALS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_COIL = "$SCREEN_VIEW_REQUEST_MATERIALS/$COIL"
        const val SCREEN_VIEW_REQUEST_MATERIALS_STICKER = "$SCREEN_VIEW_REQUEST_MATERIALS/$STICKERS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_FILM = "$SCREEN_VIEW_REQUEST_MATERIALS/$FILMS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_QUANTITY =
            "$SCREEN_VIEW_REQUEST_MATERIALS_COIL/$CONFIRM_QUANTITY"
        const val SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_COIL/$CONFIRM_REQUEST"
        const val SCREEN_VIEW_REQUEST_MATERIALS_FILM_CONFIRM_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_FILM/$CONFIRM_REQUEST"
        const val SCREEN_VIEW_REQUEST_MATERIALS_STICKER_CONFIRM_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_STICKER/$CONFIRM_REQUEST"
        const val SCREEN_VIEW_REQUEST_MATERIALS_COIL_SUCCESS_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_COIL/$SUCCESS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_FILM_SUCCESS_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_FILM/$SUCCESS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_STICKER_SUCCESS_REQUEST =
            "$SCREEN_VIEW_REQUEST_MATERIALS_STICKER/$SUCCESS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_FILM_REQUEST_IN_PROGRESS =
            "$SCREEN_VIEW_REQUEST_MATERIALS_FILM/$REQUEST_IN_PROGRESS"
        const val SCREEN_VIEW_REQUEST_MATERIALS_STICKER_REQUEST_IN_PROGRESS =
            "$SCREEN_VIEW_REQUEST_MATERIALS_STICKER/$REQUEST_IN_PROGRESS"

    }

}