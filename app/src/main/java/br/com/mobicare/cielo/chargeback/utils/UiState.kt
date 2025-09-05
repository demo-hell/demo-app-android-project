package br.com.mobicare.cielo.chargeback.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiState<out T> {
    open class Success<T>(val data: T? = null) : UiState<T>()
    class Error(val error: NewErrorMessage?): UiState<Nothing>()
    object Loading : UiState<Nothing>()
    object HideLoading : UiState<Nothing>()
    object Empty: UiState<Nothing>()
    object FilterEmpty: UiState<Nothing>()
    object MoreLoading : UiState<Nothing>()
    object HideMoreLoading : UiState<Nothing>()
}
