package br.com.mobicare.cielo.meuCadastroNovo.analytics

import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics

class MeuCadastroAnalytics {

    fun logDialogClickButton(dialogTitle: String, labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.MEU_CADASTRO),
            action = listOf(Action.POPUP, dialogTitle, Action.CLIQUE),
            label = listOf(Label.BOTAO, labelButton)
        )
    }

    fun logHomeAddEcEstablishmentScreenView(className: Class<Any>){
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to MY_REGISTER_PATH,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun logHomeAddEcEstablishmentClick(className: Class<Any>){
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to MY_REGISTER_PATH,
                Navigation.CONTENT_COMPONENT to DATA_ESTABLISHMENT,
                Navigation.CONTENT_TYPE to HomeAnalytics.LABEL,
                Navigation.CONTENT_NAME to HomeAnalytics.ADD_ESTABLISHMENT,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun screenViewEstablishment() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = ESTABLISHMENT_SCREEN_NAME
        )
    }

    fun logUpdateFantasyNameScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$ESTABLISHMENT_SCREEN_NAME/$UPDATE_FANTASY_NAME"
        )
    }

    fun logUpdateFantasyNameClick() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$ESTABLISHMENT_SCREEN_NAME/$UPDATE_FANTASY_NAME",
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to CONTENT_UPDATE,
                Navigation.CONTENT_COMPONENT to EDIT_FANTASY_NAME,
            )
        )
    }

    fun logUpdateAddressScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$ESTABLISHMENT_SCREEN_NAME/$ADDRESS_SCREEN_NAME"
        )
    }

    fun logUpdateAddressClick() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$ESTABLISHMENT_SCREEN_NAME/$ADDRESS_SCREEN_NAME",
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to UPDATE_ADDRESS,
                Navigation.CONTENT_COMPONENT to CONTENT_SAVE,
            )
        )
    }

    fun logUpdatedDataScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$ESTABLISHMENT_SCREEN_NAME/$UPDATED_DATA_SCREEN_NAME"
        )
    }

    fun logUpdatedDataClick(buttonSelected: String) {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to "$ESTABLISHMENT_SCREEN_NAME/$UPDATED_DATA_SCREEN_NAME",
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to buttonSelected,
                Navigation.CONTENT_COMPONENT to CONTENT_UPDATED_DATA_MODAL,
            )
        )
    }

    fun logEditNotAllowedScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$ESTABLISHMENT_SCREEN_NAME/$EDIT_NOT_ALLOWED_SCREEN_NAME"
        )
    }

    fun transferFlagsScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$PATH_FLAGS/$TRANSFER_FLAG/$FLAG_SCREEN_SUCCESS"
        )
    }

    fun addFlagsScreenView() {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(
            screenName = "$PATH_FLAGS/$ADD_ACCOUNT/$ADD_FLAG"
        )
    }

    companion object {
        const val MY_REGISTER_PATH = "/meu_cadastro/adicionar_estabelecimento"
        const val DATA_ESTABLISHMENT = "dados_do_estabelecimento"
        const val ESTABLISHMENT_SCREEN_NAME = "/outros/meu_cadastro/estabelecimento"
        const val UPDATE_FANTASY_NAME = "alterar_nome_fantasia"
        const val EDIT_FANTASY_NAME = "editar_nome_fantasia"
        const val CONTENT_UPDATE = "alterar"
        const val ADDRESS_SCREEN_NAME = "editar_endereco"
        const val UPDATE_ADDRESS = "alterar_endereco"
        const val CONTENT_SAVE = "salvar"
        const val UPDATED_DATA_SCREEN_NAME = "dados_atualizados"
        const val CONTENT_UPDATED_DATA_MODAL = "modal_dados_atualizados"
        const val EDIT_NOT_ALLOWED_SCREEN_NAME = "edicao_nao_permitida"
        const val PATH_FLAGS = "/outros/meu_cadastro/contas"
        const val FLAG_SCREEN_SUCCESS = "sucesso"
        const val TRANSFER_FLAG = "transferir_bandeiras"
        const val ADD_ACCOUNT = "adicionar_conta"
        const val ADD_FLAG = "adicionar_bandeira"
    }
}