package br.com.mobicare.cielo.idOnboarding.analytics

import android.content.Context
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.utils.analytics.normalize
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.DNI
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ACCESS_HELP_CENTER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CNH
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_COMPLETE_CONFIGURATION
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_CUSTOMER_RETENTION
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_DOCUMENT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_DOCUMENT_YOU_WANT_TO_SEND
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_VALIDATE_IDENTITY
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_VALIDATE_IDENTITY_BTN
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_NEW_RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_OPEN_CAMERA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_PICTURE_NOT_VALIDATE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_PICTURE_SENT_FOR_REVIEW
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_RG
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_TIPS_SELFIE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_P2
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATE_SELFIE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.APPSFLYER_AF_CONTENT_TYPE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.APPSFLYER_AF_DISPLAY_CONTENT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.APPSFLYER_AF_SCREEN_NAME
import br.com.mobicare.cielo.idOnboarding.analytics.constants.APPSFLYER_MODAL
import com.appsflyer.AppsFlyerLib

class IDOnboardingP2Analytics {

    fun logIDOnClickComeBack(screen: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(screen, Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.VOLTAR)
        )
    }

    fun logIDOnClickIUnderstood(screen: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(screen, Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.UNDERSTOOD)
        )
    }

    fun logIDOnClickNext(screen: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(screen, Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.NEXT)
        )
    }

    fun logIDOnClickHelpCenter(profile: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_COMPLETE_CONFIGURATION, normalize(profile), Action.CLIQUE),
            label = listOf(Label.BOTAO, ANALYTICS_ID_ACCESS_HELP_CENTER)
        )
    }

    fun logIDOnClickSendPictures(btn: String, profile: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_COMPLETE_CONFIGURATION, normalize(profile), Action.CLIQUE),
            label = listOf(Label.BOTAO, btn)
        )
    }

    fun logIDOnClickSendPicturesLater(btn: String, profile: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_CUSTOMER_RETENTION, normalize(profile), Action.CLIQUE),
            label = listOf(Label.BOTAO, btn)
        )
    }

    fun logIDOnSelectDocument(documentType: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_DOCUMENT_YOU_WANT_TO_SEND, Action.SELECAO),
            label = listOf(ANALYTICS_ID_DOCUMENT, documentType(documentType))
        )
    }

    fun logIDOnClickComeBackWithDocument(screen: String, documentType: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(screen, documentType(documentType), Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.VOLTAR)
        )
    }

    fun logIDOnClickNextWithDocument(screen: String, documentType: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(screen, documentType(documentType), Action.CLIQUE),
            label = listOf(Label.BOTAO, Action.NEXT)
        )
    }

    fun logIDOnSuccessSendDocument(documentType: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(
                ANALYTICS_ID_VALIDATE,
                documentType(documentType),
                Action.CALLBACK
            ),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logIDOnErrorSendDocument(documentType: String?, errorCode: String?, errorMessage: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(
                ANALYTICS_ID_VALIDATE,
                documentType(documentType),
                Action.CALLBACK
            ),
            label = listOf(Label.ERRO, normalize(errorMessage), normalize(errorCode))
        )
    }

    fun logIDOnClickOpenCamera() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_TIPS_SELFIE, Action.CLIQUE),
            label = listOf(Label.BOTAO, ANALYTICS_ID_OPEN_CAMERA)
        )
    }

    fun logIDOnSuccessSentSelfieForReview() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_PICTURE_SENT_FOR_REVIEW, Action.CALLBACK),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logIDOnSuccessSendSelfie() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_VALIDATE_SELFIE, Action.CALLBACK),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logIDOnErrorSendSelfie(errorCode: String?, errorMessage: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_VALIDATE_SELFIE, Action.CALLBACK),
            label = listOf(Label.ERRO, normalize(errorMessage), normalize(errorCode))
        )
    }

    fun logIDOnClickSendDoLater(btn: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_PICTURE_NOT_VALIDATE, Action.CLIQUE),
            label = listOf(Label.BOTAO, btn)
        )
    }

    fun logIDOnSuccessValidateP2() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_VALIDATE_P2, Action.CALLBACK),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logIDOnErrorValidateP2(errorCode: String?, errorMessage: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_VALIDATE_P2, Action.CALLBACK),
            label = listOf(Label.ERRO, normalize(errorMessage), normalize(errorCode))
        )
    }

    fun logIDModal(modal: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(Action.MODAL, Action.EXIBICAO),
            label = listOf(modal)
        )
    }

    fun logIDOnClickTakeNewPicturesModal() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.ID_ONBOARDING),
            action = listOf(ANALYTICS_ID_ERROR_VALIDATE_IDENTITY, Action.CLIQUE),
            label = listOf(Action.BOTAO, ANALYTICS_ID_ERROR_VALIDATE_IDENTITY_BTN)
        )
    }

    fun logIDScreenView(screenName: String, screenClass: Class<Any>) {
        Analytics.trackScreenView(screenName, screenClass)
    }

    fun logIDScreenViewDocumentType(screenName: String, documentType: String?, screenClass: Class<Any>) {
        Analytics.trackScreenView(screenName + documentType(documentType), screenClass)
    }

    fun logAppsFlyerP2Success(context: Context) {
        val eventParameters = HashMap<String, Any>()
        eventParameters[APPSFLYER_AF_CONTENT_TYPE] = APPSFLYER_MODAL
        eventParameters[APPSFLYER_AF_SCREEN_NAME] = ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS
        AppsFlyerLib.getInstance().logEvent(context, APPSFLYER_AF_DISPLAY_CONTENT, eventParameters)
    }

    private fun documentType(document: String?) = when (document) {
        DNI -> ANALYTICS_ID_NEW_RG
        RG -> ANALYTICS_ID_RG
        CNH -> ANALYTICS_ID_CNH
        else -> normalize(document)
    }
}