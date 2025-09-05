package br.com.mobicare.cielo.turboRegistration.analytics

import br.com.mobicare.cielo.commons.analytics.Action.CLICK
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.DESCRIPTION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.EXCEPTION_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception.STATUS_CODE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.DISPLAY_CONTENT_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MESSAGE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.WARNING
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

object TurboRegistrationAnalytics {

    private const val HOME_SCREEN = "/home"
    private const val FINISH_REGISTRATION_BUTTON_NAME = "finalizar_cadastro"
    private const val FINISH_REGISTRATION_ACTION_NAME = "finalizar"
    private const val SELF_REGISTRATION_ADDRESS_SCREEN = "/autocadastro/endereco"
    private const val SELF_REGISTRATION_MONTHLY_INCOME_SCREEN = "/autocadastro/renda_mensal"
    private const val SELF_REGISTRATION_BUSINESS_SECTOR_SCREEN = "/autocadastro/ramo"
    private const val SELF_REGISTRATION_BANK_DATA_SCREEN = "/autocadastro/dados_bancarios"
    private const val SELF_REGISTRATION_LOADING_SCREEN = "/autocadastro/loading"
    private const val SELF_REGISTRATION_DONE_SCREEN = "/autocadastro/pronto"
    private const val WARNING_WRONG_VALUE = "valor_invalido"
    private const val IN_ANALYSIS = "solitacao_em_analise"
    private const val FILL_AGAIN = "preencher_novamente"

    fun displayContentCardHomeAlert(cardTitle: String) {
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to HOME_SCREEN,
                DESCRIPTION to cardTitle.normalizeToLowerSnakeCase(),
                CONTENT_TYPE to WARNING
            )
        )
    }

    fun clickEventFinishRegistration(buttonDesc: String) {
        ga4.trackEvent(
            eventName = CLICK,
            eventsMap = mapOf(
                SCREEN_NAME to HOME_SCREEN,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to FINISH_REGISTRATION_BUTTON_NAME,
                DESCRIPTION to buttonDesc.normalizeToLowerSnakeCase()
            )
        )
    }

    fun screenViewSelfRegistrationAddress() {
        ga4.trackScreenView(SELF_REGISTRATION_ADDRESS_SCREEN)
    }

    fun screenViewSelfRegistrationMonthlyIncome() {
        ga4.trackScreenView(SELF_REGISTRATION_MONTHLY_INCOME_SCREEN)
    }

    fun displayContentMonthlyIncomeWarning() {
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_MONTHLY_INCOME_SCREEN,
                CONTENT_TYPE to WARNING,
                DESCRIPTION to WARNING_WRONG_VALUE
            )
        )
    }

    fun screenViewSelfRegistrationBusinessSector() {
        ga4.trackScreenView(SELF_REGISTRATION_BUSINESS_SECTOR_SCREEN)
    }

    fun displayContentBusinessSectorWarning() {
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_BUSINESS_SECTOR_SCREEN,
                CONTENT_TYPE to WARNING,
                DESCRIPTION to WARNING_WRONG_VALUE
            )
        )
    }

    fun screenViewSelfRegistrationBankData() {
        ga4.trackScreenView(SELF_REGISTRATION_BANK_DATA_SCREEN)
    }

    fun clickSelfRegistrationFinishButton() {
        ga4.trackEvent(
            eventName = CLICK,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_BANK_DATA_SCREEN,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to FINISH_REGISTRATION_ACTION_NAME
            )
        )
    }

    fun displayContentBankDataWarning() {
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_BANK_DATA_SCREEN,
                CONTENT_TYPE to WARNING,
                DESCRIPTION to WARNING_WRONG_VALUE
            )
        )
    }

    fun screenViewSelfRegistrationLoading() {
        ga4.trackScreenView(SELF_REGISTRATION_LOADING_SCREEN)
    }

    fun screenViewSelfRegistrationDone() {
        ga4.trackScreenView(SELF_REGISTRATION_DONE_SCREEN)
    }

    fun displayContentRegistrationFinishInAnalysis() {
        ga4.trackEvent(
            eventName = DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to HOME_SCREEN,
                CONTENT_TYPE to MESSAGE,
                DESCRIPTION to IN_ANALYSIS
            )
        )
    }

    fun exceptionSelfRegistrationError(errorMessage: String, statusCode: Int) {
        ga4.trackEvent(
            eventName = EXCEPTION_EVENT,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_LOADING_SCREEN,
                DESCRIPTION to errorMessage,
                STATUS_CODE to statusCode
            )
        )
    }

    fun clickEventTypeAgainError(errorMessage: String) {
        ga4.trackEvent(
            eventName = CLICK,
            eventsMap = mapOf(
                SCREEN_NAME to SELF_REGISTRATION_LOADING_SCREEN,
                CONTENT_TYPE to BUTTON,
                CONTENT_NAME to FILL_AGAIN,
                DESCRIPTION to errorMessage.normalizeToLowerSnakeCase()
            )
        )
    }
}