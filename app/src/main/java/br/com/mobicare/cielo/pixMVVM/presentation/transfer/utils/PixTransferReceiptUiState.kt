package br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

sealed class PixTransferReceiptUiState {
    object Loading : PixTransferReceiptUiState()
    data class Error(val error: NewErrorMessage? = null) : PixTransferReceiptUiState()
    data class Success(val result: PixTransferDetail) : PixTransferReceiptUiState()
}