package br.com.mobicare.cielo.commons.utils.token.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiTokenState {
    object Error : UiTokenState()
    object Default : UiTokenState()
    class Success(val token: String) : UiTokenState()
    class ConfigureToken(val error: NewErrorMessage) : UiTokenState()
}