package br.com.mobicare.cielo.home.presentation.analytics

import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Notification.BELL_ALERT_SHOWN_CLICK
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Notification.BELL_ALERT_SHOWN_EVENT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Notification.QUANTITY
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Search
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MESSAGE
import br.com.mobicare.cielo.commons.utils.analytics.normalize
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.utils.toLetterWithUnderScore
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ALL_READY
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_HOME_ID_P2
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SOON_ACCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SUCCESS_IN_DATA_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_WELCOME_ACCESS
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum
import br.com.mobicare.cielo.login.analytics.LoginAnalytics

class HomeGA4 {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logServiceSelectContent(
        contentComponent: String,
        contentName: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                    Navigation.CONTENT_COMPONENT to contentComponent,
                    Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                ),
        )
    }

    fun logServiceFooterSelectContent(
        screenName: String,
        contentComponent: String,
        contentName: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to "/$screenName",
                    Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                    Navigation.CONTENT_COMPONENT to contentComponent,
                    Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                ),
        )
    }

    fun logServiceHomeClick(
        contentComponent: String,
        contentName: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_TYPE to GoogleAnalytics4Values.BUTTON,
                    Navigation.CONTENT_COMPONENT to contentComponent,
                    Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                ),
        )
    }

    fun logHomeScreenView(className: Class<Any>) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeEcSwitchButtonClick(className: Class<Any>) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_COMPONENT to HomeAnalytics.COMMERCIAL_ESTABLISHMENT,
                    Navigation.CONTENT_TYPE to HomeAnalytics.BUTTON,
                    Navigation.CONTENT_NAME to HomeAnalytics.SWITCH,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeEcSelect(description: String?) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.SELECT_CONTENT_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_COMPONENT to HomeAnalytics.SWITCH_ESTABLISHMENT,
                    Navigation.CONTENT_TYPE to HomeAnalytics.LABEL,
                    Navigation.CONTENT_NAME to description.toLetterWithUnderScore(),
                ),
        )
    }

    fun logHomeAddEcScreenView(
        screenPath: String,
        className: Class<Any>,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenPath,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeAddEcClick(
        screenPath: String,
        className: Class<Any>,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenPath,
                    Navigation.CONTENT_COMPONENT to HomeAnalytics.ADD_ESTABLISHMENT,
                    Navigation.CONTENT_TYPE to HomeAnalytics.BUTTON,
                    Navigation.CONTENT_NAME to ArvAnalytics.CONFIRM,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeAddEcSuccess(
        screenPath: String,
        className: Class<Any>,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to screenPath + ADD_PATH_SUCCESS,
                    Navigation.CONTENT_COMPONENT to HomeAnalytics.ESTABLISHMENT_ADDED,
                    Navigation.CONTENT_TYPE to HomeAnalytics.BUTTON,
                    Navigation.CONTENT_NAME to HomeAnalytics.SEE_ESTABLISHMENT,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeChangeEcScreenView(className: Class<Any>) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to HomeAnalytics.ADD_CHANGE_EC_PATH,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logHomeChangeEcSearch(search: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Search.SEARCH_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Search.SEARCH_TERM to search,
                ),
        )
    }

    fun logHomeChangeEcClick(className: Class<Any>) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to HomeAnalytics.ADD_CHANGE_EC_PATH,
                    Navigation.CONTENT_COMPONENT to HomeAnalytics.SEARCH_ENGINE,
                    Navigation.CONTENT_TYPE to HomeAnalytics.LABEL,
                    Navigation.CONTENT_NAME to HomeAnalytics.ADD_ESTABLISHMENT,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logLoginInternalUserScreenView(className: Class<Any>) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to LoginAnalytics.ADD_INTERNAL_USER_PATH,
                    Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
                ),
        )
    }

    fun logLoginInternalUserExcepiton(
        code: String,
        message: String,
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to LoginAnalytics.ADD_INTERNAL_USER_PATH,
                    Exception.DESCRIPTION to message.toLetterWithUnderScore(),
                    Exception.STATUS_CODE to code,
                ),
        )
    }

    private fun logHomeCardDataAnalystDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_TYPE to MESSAGE,
                    Navigation.CONTENT_COMPONENT to ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS,
                    Exception.DESCRIPTION to ANALYTICS_ID_SOON_ACCESS,
                ),
        )
    }

    private fun logHomeCardApprovedDocumentsDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    Navigation.CONTENT_TYPE to MESSAGE,
                    Navigation.CONTENT_COMPONENT to ANALYTICS_ID_ALL_READY,
                    Exception.DESCRIPTION to ANALYTICS_ID_WELCOME_ACCESS,
                ),
        )
    }

    fun logIDHomeCardApprovedDocumentsSignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_HOME_ID_P2,
                    UserAndImpersonate.STEP to ANALYTICS_ID_SUCCESS_IN_DATA_ANALYSIS,
                ),
        )
    }

    fun statusCardID(status: IDOnboardingHomeCardStatusEnum) =
        when (status) {
            IDOnboardingHomeCardStatusEnum.DATA_ANALYSIS -> logHomeCardDataAnalystDisplay()
            IDOnboardingHomeCardStatusEnum.APPROVED_DOCUMENTS -> logHomeCardApprovedDocumentsDisplay()
            else -> normalize(status.name)
        }

    fun logBellAlertShown(quantity: Int) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = BELL_ALERT_SHOWN_EVENT,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    QUANTITY to quantity.toString(),
                ),
        )
    }

    fun logBellAlertClick(quantity: Int) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = BELL_ALERT_SHOWN_CLICK,
            eventsMap =
                mapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_HOME,
                    QUANTITY to quantity.toString(),
                ),
        )
    }

    companion object {
        const val SCREEN_VIEW_HOME = "/home"
        const val SCREEN_VIEW_OTHERS = "/outros"
        const val ADD_PATH_SUCCESS = "/sucesso"
        const val CONTENT_COMPONENT_MAIN_SERVICES = "principais_servicos"
        const val SALES = "vendas"
        const val RECEIVABLES = "recebiveis"
        const val SALES_AND_RECEIVABLES = "vendas_recebiveis"
        const val SEE_MORE_SALES = "ver_mais_vendas"
        const val ANTICIPATE_RECEIVABLES = "antecipar_recebiveis"
        const val BILLING_POSTPONED_PLAN = "faturamento_atual_do_plano_postecipado"
        const val FOLLOW_PLAN = "acompanhar_meu_plano"
        const val MENU_FOOTER = "menu_footer"
    }
}
