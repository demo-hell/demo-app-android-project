package br.com.mobicare.cielo.meuCadastroNovo.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics

object MeuCadastroGA4 {
    fun logScreenView(screenName: String) {
        Analytics.GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    object ScreenView {
        private const val SCREEN_VIEW_OTHERS_MY_PROFILE = "/outros/meu_cadastro"
        const val SCREEN_VIEW_MY_PROFILE_ESTABLISHMENT = "$SCREEN_VIEW_OTHERS_MY_PROFILE/estabelecimento"
        const val SCREEN_VIEW_MY_PROFILE_EDIT_BUSINESS_NAME = "$SCREEN_VIEW_MY_PROFILE_ESTABLISHMENT/editar_nome_fantasia"
        const val SCREEN_VIEW_MY_PROFILE_EDIT_ADDRESS = "$SCREEN_VIEW_MY_PROFILE_ESTABLISHMENT/editar_endereco"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT = "$SCREEN_VIEW_OTHERS_MY_PROFILE/contas"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT/adicionar_conta"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT_SUCCESS = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT/sucesso"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT_ADD_FLAG = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT/adicionar_bandeira"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT/transferir_bandeiras"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_SUCCESS = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER/sucesso"
        const val SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_BANK_SELECT = "$SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER/selecione_banco"
    }
}
