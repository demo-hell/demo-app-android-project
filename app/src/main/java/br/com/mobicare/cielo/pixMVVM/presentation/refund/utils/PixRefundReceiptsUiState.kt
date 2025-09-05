package br.com.mobicare.cielo.pixMVVM.presentation.refund.utils

sealed class PixRefundReceiptsUiState {
    object Loading : PixRefundReceiptsUiState()

    abstract class Success : PixRefundReceiptsUiState()
    object FullyRefunded : Success()
    object PartiallyRefunded : Success()
    object NotRefunded : Success()
    object PartiallyRefundedButExpired : Success()
    object NotRefundedButExpired : Success()
    object Unknown : Success()

    abstract class Error : PixRefundReceiptsUiState()
    object ErrorWithExpiredRefund : Error()
    object ErrorWithNotExpiredRefund : Error()
}