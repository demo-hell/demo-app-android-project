package br.com.mobicare.cielo.newRecebaRapido.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.analytics.formatTextForGA4
import br.com.mobicare.cielo.commons.utils.getTimestampNow
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.verifyNullOrBlankValue
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.FastRepayRule
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEKLY
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.analytics.FirebaseAnalytics

class RAGA4 {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun getHomeScreenName(isOffer: Boolean) =
        if (isOffer) SCREEN_VIEW_HOME_OFFER else SCREEN_VIEW_HOME

    fun getTransactionScreenName(transactionType: String) =
        SCREEN_VIEW_CUSTOM_RA.format(getTransactionName(transactionType))

    fun getPostValidityBSScreenName(transactionType: String) =
        SCREEN_VIEW_POST_VALIDITY_FEES_RA.format(getTransactionName(transactionType))

    fun getTransactionContentComponentName(transactionType: String) =
        when (transactionType) {
            ConstantsReceiveAutomatic.BOTH -> BOTH
            ConstantsReceiveAutomatic.CREDIT -> CASH
            ConstantsReceiveAutomatic.INSTALLMENT -> INSTALLMENTS
            else -> EMPTY
        }

    private fun getTransactionName(transactionType: String) =
        when (transactionType) {
            ConstantsReceiveAutomatic.BOTH -> SALES_INSTALLMENT_AND_CASH
            ConstantsReceiveAutomatic.CREDIT -> CASH
            ConstantsReceiveAutomatic.INSTALLMENT -> INSTALLMENTS
            else -> EMPTY
        }

    private fun getPeriodCategoryName(periodicitySelected: String) =
        when(periodicitySelected) {
            DAILY -> ONE_DAY
            MONTHLY -> ONE_MONTH
            WEEKLY -> ONE_WEEK
            else -> EMPTY
        }

    fun logException(screenName: String, error: NewErrorMessage? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to (error?.message.takeUnless { it.equals(DEFAULT_ERROR_MESSAGE) }  ?: error?.flagErrorCode.orEmpty()),
                Exception.STATUS_CODE to error?.httpCode.toString(),
            )
        )
    }

    fun logException(screenName: String, error: ErrorMessage? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to (error?.message.takeUnless { it.equals(DEFAULT_ERROR_MESSAGE) }  ?: error?.errorCode.orEmpty()),
                Exception.STATUS_CODE to error?.httpStatus.toString(),
            )
        )
    }


    fun logExceptionWithFastRepayRule(
        screenName: String,
        fastRepayRule: FastRepayRule?,
    ) {
        val eventsMap = mutableMapOf<String, Any>()
        eventsMap[ScreenView.SCREEN_NAME] = screenName
        fastRepayRule?.let {
            eventsMap[Exception.DESCRIPTION] = it.ruleDescription.normalizeToLowerSnakeCase()
            eventsMap[Exception.STATUS_CODE] = it.ruleCode
        }

        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = eventsMap,
        )
    }

    fun logClick(
        screenName: String,
        contentName: String,
        contentType: String = GoogleAnalytics4Values.BUTTON,
        contentComponent: String? = null
    ) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = listOfNotNull(
                ScreenView.SCREEN_NAME to screenName,
                Navigation.CONTENT_TYPE to contentType,
                Navigation.CONTENT_NAME to contentName.normalizeToLowerSnakeCase(),
                contentComponent?.let { Navigation.CONTENT_COMPONENT to contentComponent }
            ).toMap()
        )
    }

    fun logPurchase(transactionType: String, periodicitySelected: String, validity: String? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.PURCHASE_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to getSuccessScreenName(transactionType),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_ID to PURCHASE_SUCCESS_FORMAT.format(
                    getTimestampNow()
                ),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CUSTOM_AUTOMATIC_RECEIVE)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, getTransactionName(transactionType))
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY3, getPeriodCategoryName(periodicitySelected))
                    validity?.let {
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY4,
                            formatTextForGA4(it)
                        )
                    }
                }
            )
        )
    }

    fun logRABeginCheckout(transactionType: String, periodicitySelected: String, validity: String? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.BEGIN_CHECKOUT_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to getTransactionScreenName(transactionType),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CUSTOM_AUTOMATIC_RECEIVE)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                    putString(
                        FirebaseAnalytics.Param.ITEM_CATEGORY2,
                        getTransactionContentComponentName(transactionType)
                    )
                    putString(
                        FirebaseAnalytics.Param.ITEM_CATEGORY3,
                        getPeriodCategoryName(periodicitySelected)
                    )
                    validity.verifyNullOrBlankValue()?.let {
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY4,
                            formatTextForGA4(it)
                        )
                    }
                }
            )
        )
    }

    fun logRAConfirmAddPaymentInfo(transactionType: String, periodicitySelected: String, validity: String? = null) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.PaymentAndPurchase.ADD_PAYMENT_INFO_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to getConfirmScreenName(transactionType),
                GoogleAnalytics4Events.PaymentAndPurchase.TRANSACTION_TYPE to CONTRACTING
            ),
            eventsList = arrayListOf(
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_NAME, CUSTOM_AUTOMATIC_RECEIVE)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, SERVICES)
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, getTransactionName(transactionType))
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY3, getPeriodCategoryName(periodicitySelected))
                    validity.verifyNullOrBlankValue()?.let {
                        putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY4,
                            formatTextForGA4(it)
                        )
                    }
                }
            )
        )
    }

    fun logRACancel(cancellationReason: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = GoogleAnalytics4Events.Cancel.CANCEL_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to SCREEN_VIEW_RA_CANCEL,
                CANCELLATION_REASON to cancellationReason.normalizeToLowerSnakeCase()
            )
        )
    }

    fun logDisplayContent(screenName: String, contentComponent: String?, contentType: String) {
            Analytics.GoogleAnalytics4Tracking.trackEvent(
                eventName = Navigation.DISPLAY_CONTENT_EVENT,
                eventsMap = listOfNotNull(
                    ScreenView.SCREEN_NAME to screenName,
                    Navigation.CONTENT_TYPE to contentType,
                    contentComponent?.let { Navigation.CONTENT_COMPONENT to contentComponent }
                ).toMap()
            )
        }

    fun getConfirmScreenName(transactionType: String) =
        SCREEN_VIEW_CUSTOM_RA_CONFIRM.format(getTransactionName(transactionType))

    fun getSuccessScreenName(transactionType: String) =
        SCREEN_VIEW_CUSTOM_RA_SUCCESS.format(getTransactionName(transactionType))

    companion object {
        private const val SERVICES = "servicos"
        const val AUTOMATIC_RECEIVE = "recebimento_automatico"
        const val CUSTOM_AUTOMATIC_RECEIVE = "recebimento_automatico_customizado"
        private const val INTRODUCTION = "introducao"
        private const val CUSTOM = "customizado"
        const val ALREADY_HAS_RA_HIRED = "ja_possui_recebimento_automatico_contratado"
        const val YOU_ALREADY_HAS_A_PLAN_HIRED = "voce_ja_possui_um_plano_contratado"
        private const val SCREEN_VIEW_RA = "$SERVICES/$AUTOMATIC_RECEIVE"
        const val LETS_GO_LABEL = "vamos_comecar"
        const val GO_TO_FEES_AND_PLAN_LABEL = "ir_para_taxas_e_planos"
        const val SEE_PLAN_DETAILS = "ver_mais_detalhes_do_plano"
        const val SEE_FEE_BY_BRAND = "ver_taxa_por_bandeira"
        const val BOTH = "ambas"
        const val CASH = "a_vista"
        const val INSTALLMENTS = "parceladas"
        const val SALES_INSTALLMENT_AND_CASH = "vendas_parceladas_e_a_vista"
        private const val CONTRACTING = "contratacao"
        private const val ONE_DAY = "1_dia"
        private const val ONE_MONTH = "1_mes"
        private const val ONE_WEEK = "1_semana"
        private const val OFFERS = "ofertas"
        const val WANT_TO_HIRE_THIS_PRODUCT = "deseja_contratar_esse_produto"
        const val TALK_TO_SPECIALIST = "falar_com_especialista"
        const val CALL_CENTER = "central_de_atendimento"
        private const val SERVICE_CONTRACTED_ERROR = "erro_servico_contratado"
        private const val ELIGIBILITY_ERROR = "erro_elegibilidade"
        private const val GENERIC_ERROR = "erro_generico"
        const val RETENTION = "retencao"
        private const val FEES_PLANS = "taxas_planos"
        const val CANCEL_RA = "cancelar_recebimento_automatico"
        const val CONFIRM_CANCEL_RA = "confirmar_cancelamento_recebimento_automatico"
        const val CANCEL = "cancelamento"
        const val YES_I_WANT_TO_CANCEL = "sim_desejo_cancelar"
        const val WHY_YOU_WANT_TO_CANCEL = "por_que_voce_quer_cancelar"
        const val SCREEN_VIEW_INTRODUCTION = "/$SCREEN_VIEW_RA/$INTRODUCTION"
        const val SCREEN_VIEW_HOME = "/$SCREEN_VIEW_RA"
        const val SCREEN_VIEW_HOME_OFFER = "/$SCREEN_VIEW_RA/oferta"
        const val SCREEN_VIEW_HAS_RA = "/$SCREEN_VIEW_RA/$ALREADY_HAS_RA_HIRED"
        const val SCREEN_VIEW_CUSTOM_RA = "/$SCREEN_VIEW_RA/$CUSTOM/%s"
        const val SCREEN_VIEW_POST_VALIDITY_FEES_RA = "$SCREEN_VIEW_CUSTOM_RA/taxas_apos_periodo"
        const val SCREEN_VIEW_CUSTOM_RA_CONFIRM = "$SCREEN_VIEW_CUSTOM_RA/confirmar_solicitacao"
        const val SCREEN_VIEW_CUSTOM_RA_SUCCESS = "$SCREEN_VIEW_CUSTOM_RA/sucesso"
        const val SCREEN_VIEW_FEES_PLANS = "/$FEES_PLANS"
        const val SCREEN_VIEW_RA_CANCEL = "/$FEES_PLANS/$AUTOMATIC_RECEIVE/$CANCEL"
        const val SCREEN_VIEW_RA_CANCEL_OFFERS = "/$FEES_PLANS/$AUTOMATIC_RECEIVE/$CANCEL/$OFFERS"
        const val SCREEN_VIEW_CONTRACTED_SERVICE_ERROR = "/$AUTOMATIC_RECEIVE/$CONTRACTING/$SERVICE_CONTRACTED_ERROR"
        const val SCREEN_VIEW_INELIGIBLE_ERROR = "/$AUTOMATIC_RECEIVE/$CONTRACTING/$ELIGIBILITY_ERROR"
        const val SCREEN_VIEW_GENERIC_ERROR = "/$AUTOMATIC_RECEIVE/$CONTRACTING/$GENERIC_ERROR"
        const val CANCELLATION_REASON = "cancellation_reason"
        const val PURCHASE_SUCCESS_FORMAT = "t_recebimento_automatico_customizado.%s"
    }
}