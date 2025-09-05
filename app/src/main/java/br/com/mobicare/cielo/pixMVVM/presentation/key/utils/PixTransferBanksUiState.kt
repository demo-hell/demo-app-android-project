package br.com.mobicare.cielo.pixMVVM.presentation.key.utils

import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank

sealed class PixTransferBanksUiState {
    object Loading : PixTransferBanksUiState()
    object HideLoading : PixTransferBanksUiState()
    data class Success(val data: List<PixTransferBank>) : PixTransferBanksUiState()

    abstract class Error : PixTransferBanksUiState()
    object UnableToFetchBankListError : Error()
    object UnavailableServiceError : Error()
}
