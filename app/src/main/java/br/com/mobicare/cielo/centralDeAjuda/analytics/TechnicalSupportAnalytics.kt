package br.com.mobicare.cielo.centralDeAjuda.analytics

import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_OMBUDSMAN
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.OMBUDSMAN_ATTENTION
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MODAL
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.STEP
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.WARNING
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase

object TechnicalSupportAnalytics {

    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logSelectContent(screenName: String, contentComponent: String, contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_COMPONENT to contentComponent.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logSelectContentStep(screenName: String, contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to STEP,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logClick(screenName: String, contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logScreenViewProblem(screenName: String, reason: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            "$screenName/${reason.normalizeToLowerSnakeCase()}"
        )
    }

    fun logClickProblem(screenName: String, contentName: String, reason: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to "$screenName/${reason.normalizeToLowerSnakeCase()}",
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logOpenRequestClick() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to ScreenView.OPEN_REQUEST,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_COMPONENT to Values.OPEN_REQUEST,
                Navigation.CONTENT_NAME to Values.OPEN_REQUEST
            )
        )
    }

    fun logException(screenName: String, errorCode: String, errorMessage: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to errorMessage.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to errorCode,
            )
        )
    }

    fun logOmbudsmanDisplayContent() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to HELP_CENTER_OMBUDSMAN,
                Exception.DESCRIPTION to OMBUDSMAN_ATTENTION,
                Navigation.CONTENT_TYPE to MODAL,
            )
        )
    }

    fun logWarningDisplayContent(screenName: String, description: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to description,
                Navigation.CONTENT_TYPE to WARNING,
            )
        )
    }

    fun logOmbudsmanClick(contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Click.CLICK_EVENT,
            eventsMap = mapOf(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME to HELP_CENTER_OMBUDSMAN,
                Navigation.CONTENT_COMPONENT to Values.OMBUDSMAN_MODAL,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase()
            )
        )
    }

    fun buildScreenViewPath(
        screenName: String = ScreenView.SERVICES_TECHNICAL_SUPPORT,
        suffix: String? = null
    ) = if (suffix.isNullOrEmpty()) screenName else "$screenName/${suffix.normalizeToLowerSnakeCase()}"

    object Values {
        const val REQUEST_SUPPORT_ONLINE = "acessar_suporte_online"
        const val OPEN_REQUEST = "abrir_solicitacao"
        const val OMBUDSMAN_ATTENTION = "atencao_ouvidoria"
        const val OMBUDSMAN_MODAL = "modal_ouvidoria"
        const val UPDATE_ADDRESS = "atualizar_endereco"
        const val ERROR_ADDRESS = "erro_endereco"
        const val GO_INITIAL_SCREEN = "ir_para_tela_inicial"
        const val TRACK_REQUEST = "acompanhar_solicitacao"
        const val OPEN_A_TICKET = "abra_um_chamado"
    }

    object ScreenView {
        const val SERVICES_TECHNICAL_SUPPORT = "/servicos/suporte_tecnico"
        const val OPEN_REQUEST = "$SERVICES_TECHNICAL_SUPPORT/${Values.OPEN_REQUEST}"
        const val OPEN_REQUEST_SUCCESS = "$SERVICES_TECHNICAL_SUPPORT/${Values.OPEN_REQUEST}/sucesso"
        const val HELP_CENTER = "/servicos/central_de_ajuda"
        const val PATH_HELP_CENTER = "/central_de_ajuda"
        const val CALL_OPENING = "/servicos/abertura_de_chamado"
        const val REQUEST_OPENING = "/servicos/abertura_de_solicitacao"
        const val LITTLE_MACHINE = "/maquininha"
        private const val SEARCH_ENGINE = "/buscador"
        private const val RESULT = "/resultado"
        private const val WITHOUT_RESULT = "/sem_resultado"
        private const val DATA = "/dados"
        private const val OMBUDSMAN = "/ouvidoria"
        private const val MESSAGE = "/mensagem"
        private const val SUCCESS = "/sucesso"
        private const val ANSWERS = "/respostas"
        private const val QUESTIONS = "/perguntas"
        private const val PROBLEM = "/problema"
        private const val REVIEW_INFORMATIONS = "/revise_informacoes"
        private const val OPEN_TICKET = "/abertura_de_chamado"
        private const val ERROR = "/erro"
        private const val MY_REQUESTS = "/minhas_solicitacoes"
        const val CALL_OPENING_MACHINE = "$CALL_OPENING$LITTLE_MACHINE"
        const val HELP_CENTER_ANSWERS = "$HELP_CENTER$ANSWERS"
        const val HELP_CENTER_QUESTIONS = "$HELP_CENTER$QUESTIONS"
        const val TECHNICAL_SUPPORT_HELP_CENTER = "$SERVICES_TECHNICAL_SUPPORT$PATH_HELP_CENTER"
        const val PATH_PROBLEM = "$SERVICES_TECHNICAL_SUPPORT$PROBLEM"
        const val PATH_REVIEW_INFORMATIONS = "$SERVICES_TECHNICAL_SUPPORT$PROBLEM$REVIEW_INFORMATIONS"
        const val PATH_OPEN_TICKET_ERROR = "$SERVICES_TECHNICAL_SUPPORT$PROBLEM$OPEN_TICKET$ERROR"
        const val PATH_OPEN_TICKET_SUCESSO = "$SERVICES_TECHNICAL_SUPPORT$PROBLEM$OPEN_TICKET$SUCCESS"
        const val PATH_OPEN_A_TICKET = "$SERVICES_TECHNICAL_SUPPORT$PROBLEM/${Values.OPEN_A_TICKET}"
        const val TECHNICAL_SUPPORT_MY_REQUESTS = "$SERVICES_TECHNICAL_SUPPORT$MY_REQUESTS"
        const val HELP_CENTER_SEARCH = "$HELP_CENTER$SEARCH_ENGINE"
        const val HELP_CENTER_OMBUDSMAN = "$HELP_CENTER$OMBUDSMAN"
        const val HELP_CENTER_OMBUDSMAN_DATA = "$HELP_CENTER$OMBUDSMAN$DATA"
        const val HELP_CENTER_OMBUDSMAN_MESSAGE = "$HELP_CENTER$OMBUDSMAN$MESSAGE"
        const val HELP_CENTER_OMBUDSMAN_SUCCESS = "$HELP_CENTER$OMBUDSMAN$SUCCESS"
        const val HELP_CENTER_SEARCH_RESULT = "$HELP_CENTER$SEARCH_ENGINE$RESULT"
        const val HELP_CENTER_SEARCH_WITHOUT_RESULT = "$HELP_CENTER$SEARCH_ENGINE$WITHOUT_RESULT"
    }

    object MenuLabels {
        const val CATEGORIES = "categorias"
        const val FREQUENTLY_ASK_QUESTIONS = "perguntas_frequentes"
        const val CUSTOMER_SERVICE_CHANNELS = "canais_atendimento"
        const val TECHNICAL_SUPPORT = "suporte_tecnico"
    }
}