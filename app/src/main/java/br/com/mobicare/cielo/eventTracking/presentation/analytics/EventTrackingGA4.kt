package br.com.mobicare.cielo.eventTracking.presentation.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CLICK_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click.CONTENT_VALUE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.EXCEPTION_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.STATUS_CODE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_COMPONENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import com.google.firebase.analytics.FirebaseAnalytics.Event.SELECT_CONTENT


class EventTrackingGA4 {
    fun logMachineRequestException(statusName: String, errorMessage: NewErrorMessage?) {
        val description = errorMessage?.let { it.message.ifEmpty { it.message } } ?: Text.EMPTY
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$MACHINE_SCREEN_NAME/${statusName.normalizeToLowerSnakeCase()}",
                DESCRIPTION to description.normalizeToLowerSnakeCase(),
                STATUS_CODE to errorMessage?.httpCode.toStringOrEmpty()
            )
        )
    }

    fun logEmptyMachineRequestScreenView(statusName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$MACHINE_SCREEN_NAME/${statusName.normalizeToLowerSnakeCase()}/$EMPTY_MACHINE_STATUS",
            )
        )
    }

    fun logEmptyMachineRequestButtonClick(statusName: String, serviceName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = SELECT_CONTENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$MACHINE_SCREEN_NAME/${statusName.normalizeToLowerSnakeCase()}/$EMPTY_MACHINE_STATUS",
                CONTENT_COMPONENT to MACHINE_COMPONENT,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to serviceName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logMachineRequestScreenView(statusName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$MACHINE_SCREEN_NAME/${statusName.normalizeToLowerSnakeCase()}",
            )
        )
    }

    fun logMachineListClick(statusName: String, machineName: String, status: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$MACHINE_SCREEN_NAME/${statusName.normalizeToLowerSnakeCase()}",
                CONTENT_TYPE to BUTTON,
                CONTENT_COMPONENT to MACHINE_COMPONENT,
                CONTENT_NAME to machineName.normalizeToLowerSnakeCase(),
                CONTENT_VALUE to status.normalizeToLowerSnakeCase()
            )
        )
    }

    companion object {
        const val MACHINE_SCREEN_NAME = "/servicos/minhas_solicitacoes/maquininhas"
        const val EMPTY_MACHINE_STATUS = "sem_pedidos"
        const val MACHINE_COMPONENT = "maquininhas"
        const val UNDEFINED_MACHINE_NAME = "undefined"
    }
}