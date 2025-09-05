package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class PixExtractDetailUiState<out T> {
    object Loading : PixExtractDetailUiState<Nothing>()
    data class Error(val error: NewErrorMessage? = null) : PixExtractDetailUiState<Nothing>()
    data class Success<T>(val result: T) : PixExtractDetailUiState<T>()
}