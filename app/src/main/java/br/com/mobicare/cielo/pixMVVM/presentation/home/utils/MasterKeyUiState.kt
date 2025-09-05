package br.com.mobicare.cielo.pixMVVM.presentation.home.utils

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse

sealed class MasterKeyUiState {
    object Loading : MasterKeyUiState()

    data class Error(val message: String? = null) : MasterKeyUiState()

    abstract class Success : MasterKeyUiState()

    data class MasterKeyFound(
        val keys: List<PixKeysResponse.KeyItem>,
        val masterKey: PixKeysResponse.KeyItem?,
        val showAlert: Boolean
    ) : Success()

    data class MasterKeyNotFound(
        val keys: List<PixKeysResponse.KeyItem>,
        val showAlert: Boolean
    ) : Success()

    object NoKeysFound : Success()
}