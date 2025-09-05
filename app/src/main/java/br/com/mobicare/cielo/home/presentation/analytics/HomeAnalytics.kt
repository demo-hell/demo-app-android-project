package br.com.mobicare.cielo.home.presentation.analytics

import android.content.Context
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants
import br.com.mobicare.cielo.commons.utils.analytics.normalize
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.appsflyer.AppsFlyerLib

class HomeAnalytics {

    fun logScreenView(name: String, className: Class<Any>) {
        Analytics.trackScreenView(screenName = name, screenClass = className)
    }

    fun logHomeScreenViewAppsFlyer(context: Context){
        val eventParameters = HashMap<String, Any>()
        eventParameters[AppsFlyerConstants.AF_SCREEN_NAME] = SCREEN_VIEW_HOME
        AppsFlyerLib.getInstance().logEvent(context,
            AppsFlyerConstants.AF_SCREEN_VIEW, eventParameters)
    }

    fun logScreenActions(
        actionName: String,
        flowName: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(actionName, flowName, Label.CLIQUE),
            label = listOf(componentName, labelName)
        )
    }

    fun logCallbackRefreshButton(error: ErrorMessage? = null, flowName: String? = EMPTY) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(SALES_AND_RECEIVABLES, flowName, Label.CALLBACK, Action.REFRESH),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.errorMessage ?: EMPTY,
                error?.httpStatus?.toString() ?: EMPTY
            )
        )
    }

    fun logCallbackForRefreshButton(newErrorMessage: NewErrorMessage?, flowName: String? = EMPTY ){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(SALES_AND_RECEIVABLES, flowName, Label.CALLBACK, Action.REFRESH),
            label = listOf(
                if (newErrorMessage != null) Label.ERRO else Label.SUCESSO,
                newErrorMessage?.message ?: EMPTY,
                newErrorMessage?.message ?: EMPTY
            )
        )
    }

    fun logOnClickCardSendDocuments(status: IDOnboardingHomeCardStatusEnum) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(Action.CARD, Action.CLIQUE),
            label = listOf(Label.BOTAO, statusID(status))
        )
    }

    fun logScreenViewCard(status: IDOnboardingHomeCardStatusEnum) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME),
            action = listOf(Action.CARD, Action.EXIBICAO),
            label = listOf(statusID(status))
        )
    }

    private fun statusID(status: IDOnboardingHomeCardStatusEnum) = when (status) {
        IDOnboardingHomeCardStatusEnum.NONE -> CARD_SEND_DOCUMENTS
        IDOnboardingHomeCardStatusEnum.UPDATE_DATA -> CARD_UPDATE_DATA
        IDOnboardingHomeCardStatusEnum.DATA_ANALYSIS -> CARD_DATA_ANALYSIS
        IDOnboardingHomeCardStatusEnum.SEND_DOCUMENTS -> CARD_SEND_DOCUMENTS
        IDOnboardingHomeCardStatusEnum.APPROVED_DOCUMENTS -> CARD_APPROVED_DOCUMENTS
        else -> normalize(status.name)
    }

    companion object {
        const val SCREEN_VIEW_HOME = "/home"
        const val HEADER = "header"
        const val HELP = "ajuda"
        const val NOTIFICATIONS = "notificacoes"
        const val CHANGE_EC = "trocar ec"
        const val MAIN_SERVICES = "principais servicos"
        const val PLANS_AND_FEES = "taxas e planos"
        const val SALES_AND_RECEIVABLES = "vendas e recebiveis"
        const val SALES = "vendas"
        const val RECEIVABLES = "recebiveis"
        const val TOTAL_SALES_TODAY = "total de vendas hoje"
        const val LAST_SALES_TODAY = "últimas vendas hoje"
        const val SEE_MORE_SALES = "ver mais vendas"
        const val DEPOSITED_YESTERDAY = "depositado ontem"
        const val VALUE_TO_RECEIVE = "valor a receber hoje"
        const val ANTICIPATE_RECEIVABLES = "antecipar recebiveis"
        const val SEE_MORE_BRANDS = "ver mais bandeiras e plano"
        const val ERROR_ON_OPEN_HELP_CENTER_TAG = "Acao não encontrada || action_help_inicio"
        const val MODAL_RECEBA_MAIS_TAG = "modal_receba_mais"

        const val CARD_SEND_DOCUMENTS = "enviar documentos"
        const val CARD_UPDATE_DATA = "atualize seus dados"
        const val CARD_APPROVED_DOCUMENTS = "documentos aprovados"
        const val CARD_DATA_ANALYSIS = "dados em analise"
        const val ADD_CHANGE_EC_PATH = "/home/buscador/adicionar_estabelecimento"
        const val BUTTON = "button"
        const val COMMERCIAL_ESTABLISHMENT = "estabelecimento_comercial"
        const val SWITCH = "trocar"
        const val ESTABLISHMENT_ADDED = "estabelecimento_adicionado"
        const val ADD_ESTABLISHMENT = "adicionar_estabelecimento"
        const val SWITCH_ESTABLISHMENT = "trocar_estabelecimento"
        const val SEE_ESTABLISHMENT = "ver_estabelecimentos"
        const val LABEL = "label"
        const val SEARCH_ENGINE = "buscador"
    }
}