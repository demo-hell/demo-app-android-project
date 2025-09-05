package br.com.mobicare.cielo.idOnboarding.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.WARNING
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.MESSAGE
import br.com.mobicare.cielo.commons.utils.analytics.normalize
import br.com.mobicare.cielo.commons.utils.toLetterWithUnderScore
import br.com.mobicare.cielo.commons.utils.toLetterWithUnderScore
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.DNI
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ERROR_VALIDATE_PICTURE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_GENERIC_ERROR
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_BTN_TRY_AGAIN
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CNH_SELECTED
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_VALIDATE_IDENTITY_BTN
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_VALIDATE_YOUR_IDENTITY
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_FACE_PICTURE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_FOLLOW_TIPS_TO_GOOD_PICTURE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_MODAL_TIPS_TO_PICTURES_DOCUMENT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_MODAL_TRY_AGAIN_INSTRUCTIONS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_NEW_RG_SELECTED
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_RG_SELECTED
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_ERROR_VALIDATE_YOUR_IDENTITY
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_PHOTOGRAPH_CNH
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_PHOTOGRAPH_RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_CNH
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_NEW_RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_TYPE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELFIE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SEND_ALL_PHOTOS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SEND_PICTURES_LATER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SEND_PICTURES_LATER_MESSAGE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SEND_PICTURES_NOW
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TIPS_TO_PICTURES_DOCUMENT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TIPS_TO_SELFIE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TRY_AGAIN_INSTRUCTIONS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_P2_ERROR
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_WAIT_FEW_MINUTES_TO_VALIDATE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_WAIT_TO_VALIDATE

class IDOnboardingP2AnalyticsGA {

    fun logIDScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = false,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
            )
        )
    }

    fun logIDSendPicturesNowSignUp(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                UserAndImpersonate.STEP to ANALYTICS_ID_SEND_PICTURES_NOW,
            )
        )
    }

    fun logIDSendPicturesLaterClick(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_SEND_PICTURES_LATER,
            )
        )
    }

    fun logIDSendPicturesLaterDisplay(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to WARNING,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_SEND_PICTURES_LATER,
                Exception.DESCRIPTION to ANALYTICS_ID_SEND_PICTURES_LATER_MESSAGE,
            )
        )
    }

    fun logIDSelectDocumentSignUp(documentType: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_TYPE,
                UserAndImpersonate.STEP to documentType(documentType),
            )
        )
    }

    fun logIDSendPicturesDocGuideScreenView(documentType: String?) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
            )
        )
    }

    fun logIDSendPicturesDocGuideDisplay(documentType: String?) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_TIPS_TO_PICTURES_DOCUMENT,
                Exception.DESCRIPTION to ANALYTICS_ID_FOLLOW_TIPS_TO_GOOD_PICTURE,
            )
        )
    }

    fun logIDSendPicturesDocGuideSignUp(documentType: String?) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
                UserAndImpersonate.STEP to ANALYTICS_ID_TIPS_TO_PICTURES_DOCUMENT,
            )
        )
    }

    fun logIDSendPicturesDocGuideCaptureErrorDisplay(documentType: String?, description: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_TRY_AGAIN_INSTRUCTIONS,
                Exception.DESCRIPTION to description.toLetterWithUnderScore(),
            )
        )
    }

    fun logIDSendPicturesDocGuideCaptureErrorSignUp(documentType: String?) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
                UserAndImpersonate.STEP to ANALYTICS_ID_TRY_AGAIN_INSTRUCTIONS,
            )
        )
    }

    fun logIDSendPicturesDocGuideBSDisplay(documentType: String?, description: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypeSelected(documentType),
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_MODAL_TIPS_TO_PICTURES_DOCUMENT,
                Exception.DESCRIPTION to description.toLetterWithUnderScore(),
            )
        )
    }

    fun logIDSendPicturesDocGuideExcepiton(documentType: String?, code: String){
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to documentTypePhotograph(documentType),
                Exception.DESCRIPTION to ANALYTICS_GENERIC_ERROR,
                Exception.STATUS_CODE to code,
            )
        )
    }

    fun logIDSendSelfieDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS,
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_TIPS_TO_SELFIE,
                Exception.DESCRIPTION to ANALYTICS_ID_FACE_PICTURE,
            )
        )
    }

    fun logIDSendSelfieSignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS,
                UserAndImpersonate.STEP to ANALYTICS_ID_TIPS_TO_SELFIE,
            )
        )
    }

    fun logIDSendSelfieErrorDisplay(message: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS,
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_MODAL_TRY_AGAIN_INSTRUCTIONS,
                Exception.DESCRIPTION to message.toLetterWithUnderScore(),
            )
        )
    }

    fun logIDSendSelfieErrorSignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE_TIPS,
                UserAndImpersonate.STEP to ANALYTICS_ID_TRY_AGAIN_INSTRUCTIONS,
            )
        )
    }

    fun logIDSendSelfieExcepiton(code: String){
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE,
                Exception.DESCRIPTION to ANALYTICS_ERROR_VALIDATE_PICTURE,
                Exception.STATUS_CODE to code,
            )
        )
    }

    fun logIDSendSelfieInvalidClick() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_SELFIE,
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to ANALYTICS_ID_BTN_TRY_AGAIN.toLetterWithUnderScore(),
            )
        )
    }

    fun logIDValidateP2PolicyDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS,
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS,
                Exception.DESCRIPTION to ANALYTICS_ID_WAIT_TO_VALIDATE,
            )
        )
    }

    fun logIDValidateP2PolicySignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_SUCCESS,
                UserAndImpersonate.STEP to ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS,
            )
        )
    }

    fun logIDValidateP2PolicyExcepiton(code: String){
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT,
                Exception.DESCRIPTION to ANALYTICS_ID_VALIDATE_P2_ERROR,
                Exception.STATUS_CODE to code,
            )
        )
    }

    fun logIDP2SuccessDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS,
                Navigation.CONTENT_TYPE to MESSAGE,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_DATA_SENT_FOR_ANALYSIS,
                Exception.DESCRIPTION to ANALYTICS_ID_WAIT_FEW_MINUTES_TO_VALIDATE,
            )
        )
    }

    fun logIDP2ValidateIdentityErrorDisplay() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_ERROR_VALIDATE_YOUR_IDENTITY,
                Navigation.CONTENT_TYPE to WARNING,
                Navigation.CONTENT_COMPONENT to ANALYTICS_ID_ERROR_VALIDATE_YOUR_IDENTITY,
                Exception.DESCRIPTION to ANALYTICS_ID_SEND_ALL_PHOTOS,
            )
        )
    }

    fun logIDP2ValidateIdentityErrorSignUp() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to ANALYTICS_ID_SCREEN_VIEW_ERROR_VALIDATE_YOUR_IDENTITY,
                UserAndImpersonate.STEP to ANALYTICS_ID_ERROR_VALIDATE_IDENTITY_BTN.toLetterWithUnderScore(),
            )
        )
    }

    private fun documentType(document: String?) = when (document) {
        DNI -> ANALYTICS_ID_NEW_RG_SELECTED
        RG -> ANALYTICS_ID_RG_SELECTED
        CNH -> ANALYTICS_ID_CNH_SELECTED
        else -> normalize(document)
    }

    private fun documentTypeSelected(document: String?) = when (document) {
        DNI -> ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_NEW_RG
        RG -> ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_RG
        CNH -> ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_GUIDE_CNH
        else -> normalize(document)
    }

    private fun documentTypePhotograph(document: String?) = when (document) {
        DNI, RG -> ANALYTICS_ID_SCREEN_VIEW_PHOTOGRAPH_RG
        CNH -> ANALYTICS_ID_SCREEN_VIEW_PHOTOGRAPH_CNH
        else -> normalize(document)
    }
}