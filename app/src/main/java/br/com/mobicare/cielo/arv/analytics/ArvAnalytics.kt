package br.com.mobicare.cielo.arv.analytics

import br.com.mobicare.cielo.arv.utils.ArvConstants.BOTH_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.pix.constants.EMPTY

class ArvAnalytics {

    fun logScreenView(name: String, className: Class<Any>) {
        Analytics.trackScreenView(screenName = name, screenClass = className)
    }

    fun logScreenActionsWithLabelAndDetail(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String,
        detail: String,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, flowName, flowType, Label.CLIQUE),
            label = listOf(componentName, labelName, detail)
        )
    }

    fun logScreenActionsWithOneLabel(
        actionName: String,
        flowName: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, flowName, Label.CLIQUE),
            label = listOf(componentName, labelName)
        )
    }

    fun logScreenActionsWithTwoLabel(
        actionName: String,
        actionDetail: String = EMPTY,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, actionDetail, flowName, flowType, Label.CLIQUE),
            label = listOf(componentName, labelName)
        )
    }

    fun logScreenActionsWithThreeLabel(
        message: String,
        actionName: String,
        actionDetail: String = EMPTY,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(message, actionName, actionDetail, flowName, flowType, Label.CLIQUE),
            label = listOf(componentName, labelName)
        )
    }

    fun logScreenActionsGetValue(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, EMPTY, flowName, flowType, Label.CLIQUE),
            label = listOf(componentName, labelName),
            normalize = false
        )
    }

    fun logScreenActionsWithTwoLabel(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String
    ) {
        logScreenActionsWithTwoLabel(
            actionName = actionName,
            actionDetail = EMPTY,
            flowName = flowName,
            flowType = flowType,
            componentName = componentName,
            labelName = labelName
        )
    }

    fun logScreenActionsWithCheckButton(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        actionLabel: String?,
        componentName: String = Action.BOTAO,
        labelName: String?,
        flagAll: String? = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, flowName, flowType, actionLabel),
            label = listOf(componentName, labelName, flagAll)
        )
    }

    fun logScreenActionsCheckButton(
        actionName: String,
        typeEvent: String? = EMPTY,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        actionLabel: String?,
        componentName: String = Action.BOTAO,
        labelName: String?,
        flagAll: String? = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, typeEvent, flowName, flowType, actionLabel),
            label = listOf(componentName, labelName, flagAll)
        )
    }

    fun logScreenActionsDialog(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String?,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, flowName, flowType),
            label = listOf(componentName, labelName)
        )
    }

    fun logScreenActionsWithFlowDialog(
        actionName: String,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        negotiationType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String?,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, flowType, flowName, negotiationType),
            label = listOf(componentName, labelName)
        )
    }

    fun logScreenActionsOnWithFlowDialog(
        actionName: String,
        actionType: String? = EMPTY,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        negotiationType: String? = EMPTY,
        labelName: String?,
        message: String? = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, actionType, flowName, flowType, negotiationType),
            label = listOf(labelName, message)
        )
    }

    fun logScreenDialogShow(
        componentName: String,
        actionName: String,
        flowName: String? = EMPTY,
        arvNegotiationType: String? = EMPTY,
        dialogMessage: String = EMPTY,
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(componentName, actionName, flowName, arvNegotiationType),
            label = listOf(dialogMessage),
            normalize = false
        )
    }

    fun logScreenActionsWithDates(
        actionName: String,
        periodLabel: String? = EMPTY,
        flowName: String? = EMPTY,
        flowType: String? = EMPTY,
        componentName: String = Action.BOTAO,
        labelName: String,
        labelNameTwo: String? = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(actionName, periodLabel, flowName, flowType, INTERACT),
            label = listOf(componentName, labelName, labelNameTwo),
            normalize = false
        )
    }

    fun logCallbackEvent(flowName: String? = EMPTY, flowNameTwo: String? = EMPTY, success: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(SIMULATE_LOAD, flowName, flowNameTwo, Label.CALLBACK),
            label = listOf(success)
        )
    }

    fun logCallbackError(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        error: NewErrorMessage? = null
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, Label.CALLBACK),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.message ?: EMPTY,
                error?.httpCode?.toString() ?: EMPTY
            )
        )
    }

    fun logCallbackErrorEvent(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        typeScreen: String,
        error: NewErrorMessage? = null
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, typeScreen),
            label = listOf(if (error != null) Label.ERRO else Label.SUCESSO,
                error?.message ?: EMPTY,
                error?.httpCode?.toString() ?: EMPTY
            )
        )
    }

    fun logCallbackErrorButtonEvent(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        typeScreen: String,
        error: NewErrorMessage? = null,
        nameButton: String? = EMPTY
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, typeScreen),
            label = listOf(
                Label.ERRO,
                error?.message ?: EMPTY ,
                error?.httpCode?.toString() ?: EMPTY,
                Label.BOTAO,
                nameButton
            )
        )
    }

    fun logEventCallback(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        typeScreen: String,
        label: String?,
        error: NewErrorMessage? = null
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, typeScreen, label),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.message ?: EMPTY,
                error?.httpCode?.toString() ?: EMPTY
            )
        )
    }

    fun logEventCallbackNewArv(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        typeScreen: String,
        label: String?,
        error: NewErrorMessage? = null
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, typeScreen, label),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.message ?: EMPTY,
                error?.httpCode?.toString() ?: EMPTY
            )
        )
    }

    fun logEventLoadCallback(
        flowScreen: String?,
        flowName: String? = EMPTY,
        negotiationTypeArv: String? = EMPTY,
        typeScreen: String?,
        label: String?,
        error: NewErrorMessage? = null
    ) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.HOME_NEW_ARV),
            action = listOf(flowScreen, flowName, negotiationTypeArv, typeScreen, label),
            label = listOf(
                if (error != null) Label.ERRO else Label.SUCESSO,
                error?.message ?: EMPTY,
                error?.httpCode?.toString() ?: EMPTY
            )
        )
    }

    fun negotiationType(negotiationTypeArv: String?): String {
        return when (negotiationTypeArv) {
            CIELO_NEGOTIATION_TYPE -> RECEIVABLES_CIELO
            MARKET_NEGOTIATION_TYPE -> RECEIVABLES_MARKET
            BOTH_NEGOTIATION_TYPE -> RECEIVABLES_BOTH
            else -> EMPTY
        }
    }


    companion object {
        const val SCREEN_VIEW_ARV_HOME = "/antecipacao de recebiveis novo/modelo de antecipacao"
        const val SCREEN_VIEW_ARV_SINGLE_ANTICIPATION =
            "/antecipacao de recebiveis novo/antecipacao avulsa"
        const val SCREEN_VIEW_ARV_SCHEDULED_ANTICIPATION =
            "/antecipacao de recebiveis novo/antecipacao/programada"
        const val SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_PERIOD =
            "/antecipacao de recebiveis novo/revisar e solicitar antecipacao por periodo/avulsa"
        const val SCREEN_VIEW_ARV_REVIEW_ANTICIPATION_WITH_VALUE =
            "/antecipacao de recebiveis novo/revisar e solicitar antecipacao por valor/avulsa"
        const val SCREEN_VIEW_ARV_SCHEDULED_REVIEW_ANTICIPATION =
            "/antecipacao de recebiveis novo/revisar e solicitar antecipacao/programada"
        const val SCREEN_VIEW_ARV_EDIT_FLAG_BRAND_WITH_PERIOD =
            "/antecipacao de recebiveis novo/edicao de bandeiras por periodo/avulsa"
        const val SCREEN_VIEW_ARV_EDIT_FLAG_BRAND_WITH_VALUE =
            "/antecipacao de recebiveis novo/edicao de bandeiras por valor/avulsa"
        const val SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_VALUE =
            "/antecipacao de recebiveis novo/solicitacao efetivada com sucesso/por valor/avulsa"
        const val SCREEN_VIEW_ARV_SUCCESS_REQUEST_WITH_PERIOD =
            "/antecipacao de recebiveis novo/solicitacao efetivada com sucesso/por periodo/avulsa"
        const val SCREEN_VIEW_ARV_SCHEDULED_SUCCESS_REQUEST =
            "/antecipacao de recebiveis novo/solicitacao efetivada com sucesso/programada"
        const val SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_PERIOD =
            "/antecipacao de recebiveis novo/parece que tivemos um problema/mfa/por periodo/avulsa"
        const val SCREEN_VIEW_ARV_MFA_PROBLEM_WITH_VALUE =
            "/antecipacao de recebiveis novo/parece que tivemos um problema/mfa/por valor/avulsa"
        const val SCREEN_VIEW_ARV_SCHEDULED_MFA_PROBLEM =
            "/antecipacao de recebiveis novo/parece que tivemos um problema/mfa/programada"
        const val SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE =
            "/antecipacao de recebiveis novo/por valor que deseja receber/avulsa"
        const val SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_PERIOD =
            "/antecipacao de recebiveis novo/nao ha saldo suficiente/mfa/por periodo/avulsa"
        const val SCREEN_VIEW_ARV_NOT_ENOUGH_BALANCE_WITH_VALUE =
            "/antecipacao de recebiveis novo/nao ha saldo suficiente/mfa/por valor/avulsa"
        const val SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_PERIOD =
            "/antecipacao de recebiveis novo/edicao de bandeiras/load por periodo/avulsa"
        const val SCREEN_VIEW_ARV_EDIT_FLAGS_LOAD_WITH_VALUE =
            "/antecipacao de recebiveis novo/edicao de bandeiras/load por valor/avulsa"
        const val SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_LOAD =
            "/antecipacao de recebiveis novo/callback por valor que deseja receber/load/avulsa"
        const val SCREEN_VIEW_ARV_SIMULATED_WITH_PERIOD_ERROR = "/antecipacao de recebiveis novo/parece que tivemos um problema/edicao de bandeiras por periodo/avulsa"
        const val SCREEN_VIEW_ARV_SIMULATED_WITH_VALUE_ERROR =
            "/antecipacao de recebiveis novo/parece que tivemos um problema/load por valor/avulsa"
        const val MODEL_ANTICIPATION = "modelo de antecipacao"
        const val SINGLE_ARV = "avulsa"
        const val SCHEDULE_ARV = "programada"
        const val CANCEL = "cancelar"
        const val SEE_ALL_HYSTORC = "ver historico completo"
        const val ENGAGE = "contratar"
        const val HELP = "ajuda"
        const val SIMULATE_ARV_ANTICIPATION = "antecipacao"
        const val ARV_REVIEW_AND_REQUEST_ANTICIPATION = "revisar e solicitar antecipacao"
        const val ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_PERIOD =
            "revisar e solicitar antecipacao por periodo"
        const val ARV_REVIEW_AND_REQUEST_ANTICIPATION_WITH_VALUE =
            "revisar e solicitar antecipacao por valor"
        const val RECEIVABLES_CIELO = "recebiveis cielo"
        const val RECEIVABLES_MARKET = "recebiveis outras maquininhas"
        const val RECEIVABLES_BOTH = "ambas agendas"
        const val ANTICIPATION_WITH_PERIOD = "antecipar valor do periodo"
        const val SIMULATED_ANTICIPATION_WITH_VALUE = "simule o valor que deseja receber"
        const val SIMULATE_FILL_PERIOD = "preeencher periodo"
        const val INTERACT = "interacao"
        const val FIELD_OF = "campo de"
        const val FIELD_UNTIL = "campo ate"
        const val SIMULATE_LOAD = "carregamento da simulacao"
        const val TAG_SUCCESS_EMPTY_VALUE = "nao ha valor disponivel para antecipar"
        const val SHOW_FLAG_BRANDS_LIST = "ver lista de bandeiras"
        const val EDIT_FLAG_BRANDS = "editar bandeiras"
        const val EDITING_FLAGS = "edicao de bandeiras"
        const val EDITING_FLAGS_WITH_PERIOD = "edicao de bandeiras por periodo"
        const val EDITING_FLAGS_WITH_VALUE = "edicao de bandeiras por valor"
        const val CHANGE_ADDRESS = "alterar domicilio"
        const val BACK_BEFORE_COMPLETE = "voltar antes de concluir"
        const val COMPLETE_REQUEST = "concluir solicitacao"
        const val REQUEST_ANTICIPATION = "revisar e solicitar antecipacao"
        const val SAVE_CHANGES = "salvar alteracoes"
        const val SEE_MORE_BTN = "ver mais"
        const val CHECKED = "marcar"
        const val UNCHECKED = "desmarcar"
        const val ALL_ARV = "todos"
        const val MODAL_EXHIBITION = "modal | exibicao"
        const val MODAL_CLICK = "modal | clique"
        const val EXHIBITION = "exibicao"
        const val ATTENTION = "atencao"
        const val SELECT_AT_LEAST_ONE_FLAG = "selecione ao menos uma bandeira"
        const val SELECT_ACCOUNT_BANK_TO_RECEIPT = "selecionar conta para recebimento"
        const val CONFIRM = "confirmar"
        const val ATENTION_INFO_BEFORE_TO_CONFIRM = "atencao as informacoes antes de confirmar"
        const val REQUEST_MADE_SUCCESSFULLY = "solicitacao efetivada com sucesso"
        const val TRACK_STATUS = "acompanhar status"
        const val NOT_ENOUGH_BALANCE_WITH_PERIOD = "nao ha saldo sulficiente | mfa | por periodo"
        const val NOT_ENOUGH_BALANCE_WITH_VALUE = "nao ha saldo sulficiente | mfa | por valor"
        const val HAVE_A_PROBLEM_MFA_WITH_PERIOD = "parece que tivemos um problema | mfa | por periodo"
        const val HAVE_A_PROBLEM_MFA_WITH_VALUE = "parece que tivemos um problema | mfa | por valor"
        const val HAVE_A_PROBLEM = "parece que tivemos um problema"
        const val NOT_ENOUGH_BALANCE = "nao ha saldo sulficiente"
        const val RESIDENCE = "domicilio"
        const val ANTICIPATION = "antecipacao"
        const val CONTINUE = "continuar"
        const val MFA = "mfa"
        const val RELOAD = "recarregar"
        const val EDIT_FLAGS_LOAD_WITH_PERIOD = "load por periodo"
        const val EDIT_FLAGS_LOAD_WITH_VALUE = "load por valor"
        const val PERIOD_FLOW = "por periodo"
        const val VALUE_FLOW = "por valor"
        const val VALUE_WITH_DESIRE_WITHDRAW = "por valor que deseja receber"
        const val BUTTON_SIMULATED = "botao | simular"
        const val NOT_POSSIBLE_SIMULATED_VALUE_ABOVE_LIQUID_VALUE =
            "nao e possivel simular um valor maior que o valor total liquido"
        const val TRY_AGAIN = "tentar novamente"
        const val VALUE_WITH_DESIRE_WITHDRAW_CALLBACK = "callback por valor que deseja receber"
        const val LOAD = "load"


    }
}