package br.com.mobicare.cielo.tapOnPhone.analytics

import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.utils.analytics.normalize
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY

class TapOnPhoneAnalytics {

    fun logScreenView(name: String, className: Class<Any>) {
        Analytics.trackScreenView(screenName = name, screenClass = className)
    }

    fun logCallbackSuccess(
        flow: String,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CIELO_TAP),
            action = listOf(flow, Action.CALLBACK),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logCallbackError(
        flow: String,
        errorMessage: String,
        errorCode: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CIELO_TAP),
            action = listOf(flow, Action.CALLBACK),
            label = listOf(Label.ERRO, errorMessage, errorCode)
        )
    }

    fun logStatusCallback(
        isError: Boolean = false,
        status: String = EMPTY,
        errorMessage: String = EMPTY,
        errorCode: String = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CIELO_TAP),
            action = listOf(API_STATUS, Action.CALLBACK),
            label = if(isError) listOf(Label.ERRO, errorMessage, errorCode) else listOf(Label.SUCESSO, status)
        )
    }

    fun logOrderRequestCallback(
        isError: Boolean = false,
        errorMessage: String = EMPTY,
        errorCode: String = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CIELO_TAP),
            action = listOf(TERM_ACCEPT, MenuPreference.instance.getUserObj()?.mainRole, Action.CALLBACK),
            label = if(isError) listOf(Label.ERRO, errorMessage, errorCode) else listOf(Label.SUCESSO)
        )
    }

    fun logScreenActions(
        flowName: String,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        logScreenActions(
            flowName = flowName,
            componentName = componentName,
            labelList = listOf(labelName)
        )
    }

    fun logScreenActions(
        flowName: String,
        componentName: String = Action.BOTAO,
        labelList: List<String>
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CIELO_TAP),
            action = listOf(flowName, Label.CLIQUE),
            label = listOf(componentName) + labelList
        )
    }

    fun logNFCNotSupported(gaFlowDetails: String, className: Class<Any>) {
        logScreenView(NFC_ERROR_SCREEN_PATH.format(gaFlowDetails), className)
    }

    fun logAndroidIsNotSupported(gaFlowDetails: String, className: Class<Any>) {
        logScreenView(ANDROID_ERROR_SCREEN_PATH.format(gaFlowDetails), className)
    }

    fun logDisableDeveloperMode(gaFlowDetails: String, className: Class<Any>) {
        logScreenView(DISABLE_DEVELOPER_MODE_SCREEN_PATH.format(gaFlowDetails), className)
    }

    fun logEnableNfcOnSettings(gaFlowDetails: String, className: Class<Any>) {
        logScreenView(ENABLE_NFC_ON_SETTINGS_SCREEN_PATH.format(gaFlowDetails), className)
    }

    fun logOnboardingStep(stepNumber: Int, stepTitle: String, className: Class<Any>) {
        logScreenView(ONBOARDING_SCREEN_PATH.format(stepNumber + ONE, normalize(stepTitle)), className)
    }

    fun logPaymentReceipt(paymentMethod: String, className: Class<Any>) {
        logScreenView(TRANSACTIONAL_PAYMENT_FLOW_RECEIPT_PATH.format(paymentMethod), className)
    }

    fun logPaymentSaleNotMade(paymentMethod: String, className: Class<Any>) {
        logScreenView(TRANSACTIONAL_PAYMENT_FLOW_SALE_NOT_MADE_PATH.format(paymentMethod), className)
    }

    fun logTransactionError(
        paymentMethod: String,
        errorMessage: String = GENERIC_ERROR,
        errorCode: String = EMPTY,
        className: Class<Any>
    ) {
        logScreenView(TRANSACTIONAL_PAYMENT_FLOW_ERROR_PATH.format(paymentMethod, errorMessage, errorCode), className)
    }

    companion object {
        private const val ERROR_WARN = "aviso de problema"

        const val TRANSACTIONAL = "transacional"
        const val ACCREDITATION = "credenciamento"
        const val ENABLEMENT = "habilitacao"
        const val OFFER = "oferta"
        const val FEES = "taxas por parcela"
        const val API_STATUS = "status da api"
        const val STATUS = "status"
        const val OFFER_LOAD = "carregamento oferta"
        const val TERM_ACCEPT = "termo de aceite"
        const val TERM_AND_CONDITIONS = "termos e condicoes de uso"
        const val MAKE_SALE = "realizar venda"
        const val DEBIT = "debito"
        const val CREDIT = "credito a vista"
        const val INSTALLMENT_CREDIT = "credito parcelado"
        const val SELL = "vender"
        const val NEW_SALE = "nova venda"
        const val PAYMENT_RECEIPT = "comprovante de pagamento"
        const val SALE_VALUE = "valor da venda"
        const val GENERIC_ERROR = "erro generico"
        const val CANCELED_SALE = "venda cancelada"
        const val EXPIRED_TIME = "tempo expirado"
        const val CALL_TO_CALL_CENTER = "ligar para central"
        const val ACCESS_SETTINGS = "acessar configuracoes"
        const val CANNOT_PROCEED_WITH_ACCREDITATION = "nao podemos prosseguir no credenciamento"
        const val DISABLE_DEVELOPER_MODE = "desabilite o modo desenvolvedor"
        const val ENABLE_NFC_ON_SETTINGS = "habilite o nfc nas configuracoes"
        const val DEACTIVATED_ON_YOUR_ACCOUNT = "desativado na sua conta"
        const val DEACTIVATED_SCREEN_PATH = "/${Category.CIELO_TAP}/$DEACTIVATED_ON_YOUR_ACCOUNT"
        const val API_STATUS_ERROR_SCREEN_PATH = "/${Category.CIELO_TAP}/$STATUS/$ERROR_WARN"
        const val ACCREDITATION_CANNOT_PROCEED_SCREEN_PATH = "/${Category.CIELO_TAP}/$CANNOT_PROCEED_WITH_ACCREDITATION"
        const val ACCREDITATION_OFFER_LOAD_ERROR_SCREEN_PATH = "/${Category.CIELO_TAP}/$ACCREDITATION/$OFFER_LOAD/$ERROR_WARN"
        const val ACCREDITATION_TERM_ACCEPT_ERROR_SCREEN_PATH = "/${Category.CIELO_TAP}/$ACCREDITATION/aceite do termo/$ERROR_WARN"
        const val NFC_ERROR_SCREEN_PATH = "/${Category.CIELO_TAP}/%s/seu celular nao possui nfc"
        const val ANDROID_ERROR_SCREEN_PATH = "/${Category.CIELO_TAP}/%s/seu celular nao e compativel"
        const val ENABLE_NFC_ON_SETTINGS_SCREEN_PATH = "/${Category.CIELO_TAP}/%s/$ENABLE_NFC_ON_SETTINGS"
        const val DISABLE_DEVELOPER_MODE_SCREEN_PATH = "/${Category.CIELO_TAP}/%s/$DISABLE_DEVELOPER_MODE"
        const val OFFER_SCREEN_PATH = "${Category.CIELO_TAP}/$OFFER"
        const val FEES_SCREEN_PATH = "${Category.CIELO_TAP}/$FEES"
        const val ONBOARDING_NUMBER = "onboarding%d"
        const val ONBOARDING_SCREEN_PATH = "/${Category.CIELO_TAP}/$ONBOARDING_NUMBER/%s"
        const val ACCREDITATION_ORDER_SUCCESS_SCREEN_PATH = "/${Category.CIELO_TAP}/solicitacao para habilitar o cielo tap realizada/pedido"
        const val ACCREDITATION_ORDER_IN_PROGRESS_SCREEN_PATH = "/${Category.CIELO_TAP}/solicitacao para habilitar o cielo tap realizada"
        const val ACCREDITATION_NOT_ACTIVE_SCREEN_PATH = "/${Category.CIELO_TAP}/a habilitacao do cielo tap esta sendo feita"
        const val ACCREDITATION_TERM_AND_CONDITIONS_SCREEN_PATH = "/${Category.CIELO_TAP}/$TERM_AND_CONDITIONS"
        const val ENABLEMENT_EXTENSION_ACTIVATED_PATH = "/${Category.CIELO_TAP}/$ENABLEMENT/extensao ativada com sucesso"
        const val ENABLEMENT_EXTENSION_ERROR_PATH = "/${Category.CIELO_TAP}/$ENABLEMENT/$ERROR_WARN"
        const val TRANSACTIONAL_SDK_INITIALIZE_ERROR_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/$ERROR_WARN"
        const val TRANSACTIONAL_SALE_VALUE_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/$SALE_VALUE"
        const val TRANSACTIONAL_MAKE_SALE_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/$MAKE_SALE"
        const val TRANSACTIONAL_PAYMENT_FLOW_RECEIPT_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/%s/comprovante"
        const val TRANSACTIONAL_PAYMENT_FLOW_SALE_NOT_MADE_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/%s/venda nao realizada"
        const val TRANSACTIONAL_PAYMENT_FLOW_ERROR_PATH = "/${Category.CIELO_TAP}/$TRANSACTIONAL/%s/%s/%s"
    }
}