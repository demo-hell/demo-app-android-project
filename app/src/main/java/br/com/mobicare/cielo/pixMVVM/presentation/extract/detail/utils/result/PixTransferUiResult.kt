package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result

sealed class PixTransferUiResult {
    open class TransferSent : PixTransferUiResult()
    open class TransferReceived : PixTransferUiResult()
    open class TransferCanceled : PixTransferUiResult()
    open class TransferInProcess : PixTransferUiResult()

    object FeeTransferInProcess : TransferInProcess()
    object FeeTransferSent : TransferSent()
    object FeeTransferCanceled : TransferCanceled()

    object AutomaticTransferInProcess : TransferInProcess()
    object AutomaticTransferSent : TransferSent()
    object AutomaticTransferCanceled : TransferCanceled()

    open class QrCodeTransferSent : TransferSent()
    open class QrCodeTransferReceived : TransferReceived()

    object QrCodeWithdrawalTransferSent : QrCodeTransferSent()
    object QrCodeWithdrawalTransferReceived : QrCodeTransferReceived()

    object QrCodeChangeTransferSent : QrCodeTransferSent()
    object QrCodeChangeTransferReceived : QrCodeTransferReceived()
}
