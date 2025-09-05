package br.com.mobicare.cielo.meuCadastroNovo.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.Message

sealed class UserDataChangeUiState {
    class UserValidateSuccess(val response: String) : UserDataChangeUiState()
    class UserValidateEmailError(val message: String): UserDataChangeUiState()
    class UserValidatePasswordError(val message: String): UserDataChangeUiState()
    class UserValidatePhoneError(val message: String): UserDataChangeUiState()
    class GenericError(val message: String?): UserDataChangeUiState()
    class UserUpdateSuccess(val response: String) : UserDataChangeUiState()
    class UserUpdateError(val message: String?) : UserDataChangeUiState()
}