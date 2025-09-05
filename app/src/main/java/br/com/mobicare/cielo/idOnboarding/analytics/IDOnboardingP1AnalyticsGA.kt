package br.com.mobicare.cielo.idOnboarding.analytics

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.analytics.Action.NEXT
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.ICON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.WARNING
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MESSAGE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.PHONE_NUMBER
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_BLOCKED_ACCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CURRENTLY_BLOCKED
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_BLOCKED_ACCESS_BY_STONE_AGE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CALL_HELP_CENTER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CONTACT_US
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_EDIT_EMAIL
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_EDIT_FULL_NAME
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_EDIT_PHONE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INCORRECT_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INSERT_MAX_TRIES_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INSERT_NEW_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INSERT_REGULARIZED_MY_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INSERT_REGULARIZE_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_INSERT_VALIDATION_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_IRREGULAR_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_LAST_DAYS_VALIDATION
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_START_VALIDATION
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_UNABLE_TO_VALIDATE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_LATER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_YOUR_DATA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATION_EMAIL
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATION_FULL_NAME
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATION_PHONE

class IDOnboardingP1AnalyticsGA {

    fun logIDScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
            )
        )
    }

    fun logIDScreenViewStep(screenName: String, step: Int) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName + step.toString(),
            )
        )
    }

    fun logIDStartValidationSignUp(screenName: String, step: Int) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName + step.toString(),
                UserAndImpersonate.STEP to ANALYTICS_ID_START_VALIDATION,
            )
        )
    }

    fun logIDValidateSignUp(screenName: String, step: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                UserAndImpersonate.STEP to step,
            )
        )
    }

    fun logIDValidateDisplay(screenName: String, code: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapFilterStatusCode(screenName,
                ANALYTICS_ID_VALIDATE_YOUR_DATA, WARNING, code, ANALYTICS_ID_BLOCKED_ACCESS)
        )
    }

    fun logIDValidateClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATE_YOUR_DATA,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_VALIDATE_LATER,
            )
        )
    }

    fun logIDContinueValidationDisplay(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATE_YOUR_DATA,
                Navigation.CONTENT_TYPE to MESSAGE,
            )
        )
    }

    fun logIDContinueValidationClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATE_YOUR_DATA,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to CONTINUAR,
            )
        )
    }

    fun logIDBlockedAccessDisplay(screenName: String, code: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapFilterStatusCode(screenName, ANALYTICS_ID_CURRENTLY_BLOCKED, WARNING,
                code,ANALYTICS_ID_BLOCKED_ACCESS_BY_STONE_AGE,
            )
        )
    }

    fun logIDBlockedAccessCallHelpCenterClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_CURRENTLY_BLOCKED,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_CALL_HELP_CENTER,
            )
        )
    }

    fun logIDCallHelpCenterDisplay(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_CALL_HELP_CENTER,
                Navigation.CONTENT_TYPE to PHONE_NUMBER,
            )
        )
    }

    fun logIDCallHelpCenterClick(screenName: String, button: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_CALL_HELP_CENTER,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to button.removeNonNumbers(),
            )
        )
    }

    fun logIDValidateCpfClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_INCORRECT_CPF,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_INSERT_NEW_CPF,
            )
        )
    }

    fun logIDValidateCpfDisplay(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_INCORRECT_CPF,
                Navigation.CONTENT_TYPE to MESSAGE,
            )
        )
    }

    fun logIDValidateNewCpfClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_INSERT_VALIDATION_CPF,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to NEXT,
            )
        )
    }

    fun logIDIrregularCpfDisplay(screenName: String, code: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapFilterStatusCode(screenName, ANALYTICS_ID_IRREGULAR_CPF, WARNING,
            code, ANALYTICS_ID_INSERT_REGULARIZE_CPF,
            )
        )
    }

    fun logIDIrregularCpfClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_IRREGULAR_CPF,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_INSERT_REGULARIZED_MY_CPF,
            )
        )
    }

    fun logIDMaxTriesDisplay(screenName: String, code: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapFilterStatusCode(screenName, ANALYTICS_ID_INSERT_MAX_TRIES_CPF, WARNING,
                code, EMPTY,
            )
        )
    }

    fun logIDUnableValidateDisplay(screenName: String, code: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapFilterStatusCode(screenName, ANALYTICS_ID_UNABLE_TO_VALIDATE, WARNING,
            code, ANALYTICS_ID_CONTACT_US,
            )
        )
    }

    fun logIDUnableValidateClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_UNABLE_TO_VALIDATE,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_CALL_HELP_CENTER,
            )
        )
    }

    fun logIDEditNameClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATION_FULL_NAME,
                Navigation.CONTENT_TYPE to ICON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_EDIT_FULL_NAME,
            )
        )
    }

    fun logIDEditEmailClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATION_EMAIL,
                Navigation.CONTENT_TYPE to ICON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_EDIT_EMAIL,
            )
        )
    }

    fun logIDEditPhoneClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_VALIDATION_PHONE,
                Navigation.CONTENT_TYPE to ICON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_EDIT_PHONE,
            )
        )
    }

    fun logIDValidateCodeClick(screenName: String, contentName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName,
            )
        )
    }

    fun logIDValidateLastDaysDisplay(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_LAST_DAYS_VALIDATION,
                Navigation.CONTENT_TYPE to WARNING,
            )
        )
    }

    private fun mapFilterStatusCode(screenName: String, component: String, type: String, code: String, description: String): Map<String, Any> {
        var eventsMapValues = mapOf(
            ScreenView.SCREEN_NAME to screenName,
            Navigation.CONTENT_COMPONENT to component,
            Navigation.CONTENT_TYPE to type,
            Exception.STATUS_CODE to code,
            Exception.DESCRIPTION to description,
        )

        if (code.isEmpty()){
            eventsMapValues = eventsMapValues.filterKeys { it != Exception.STATUS_CODE}
        }

        if (description.isEmpty()){
            eventsMapValues = eventsMapValues.filterKeys { it != Exception.DESCRIPTION}
        }
        return eventsMapValues
    }
}