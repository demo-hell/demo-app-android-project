package br.com.mobicare.cielo.pixMVVM.presentation.account.utils

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse

sealed class PixScheduledTransferUiState {
    data class Success(val response: PixScheduledSettlementResponse) : PixScheduledTransferUiState()
    object Error : PixScheduledTransferUiState()
}
