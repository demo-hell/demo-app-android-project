package br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState

class PixRefundDetailSuccessStateHandler {

    operator fun invoke(data: PixRefundDetail?): PixRefundDetailUiState.Success =
        when (data?.transactionStatus) {
            PixTransactionStatus.EXECUTED -> PixRefundDetailUiState.StatusExecuted
            PixTransactionStatus.PENDING,
            PixTransactionStatus.PROCESSING -> PixRefundDetailUiState.StatusPending
            else -> PixRefundDetailUiState.StatusNotExecuted
        }

}