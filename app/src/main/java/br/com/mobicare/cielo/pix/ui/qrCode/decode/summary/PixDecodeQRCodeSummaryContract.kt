package br.com.mobicare.cielo.pix.ui.qrCode.decode.summary

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.PixTransferResponse
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse

interface PixDecodeQRCodeSummaryContract {

    interface View : BaseView {
        fun onErrorPayQRCode(onGenericError: () -> Unit)
        fun onSuccessPayQRCode(onAction: () -> Unit)
        fun onSuccessfulPayment(
            transferResponse: PixTransferResponse
        )

        fun onTransactionInProcess()
        fun onPaymentError()

        fun onErrorDecode(errorMessage: ErrorMessage? = null)
        fun onSuccessDecode(decode: QRCodeDecodeResponse, scheduling: String?)
        fun onSuccessScheduling(transferResponse: PixTransferResponse)
    }

    interface Presenter {
        fun getUsername(): String
        fun isAllowedChangeValue(decode: QRCodeDecodeResponse): Boolean
        fun getTransferValue(decode: QRCodeDecodeResponse): Double
        fun getBankName(decode: QRCodeDecodeResponse): String?
        fun onPayQRCode(
            otp: String,
            message: String?,
            date: String?,
            amount: Double?,
            decodeResponse: QRCodeDecodeResponse?,
            fingerprint: String
        )

        fun onDecode(qrcode: String? = null, scheduling: String? = null)

        fun onResume()
        fun onPause()
    }
}