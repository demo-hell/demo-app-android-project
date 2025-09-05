package br.com.mobicare.cielo.home.presentation.postecipado.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class PostecipadoUiState<out T> {
    open class Success<T>(val data: T? = null) : PostecipadoUiState<T>()
    class Error(val error: NewErrorMessage? = null): PostecipadoUiState<Nothing>()
    object ShowLoading: PostecipadoUiState<Nothing>()
    object HideLoading: PostecipadoUiState<Nothing>()
    object Empty : PostecipadoUiState<Nothing>()
}
