package br.com.mobicare.cielo.pix.ui.qrCode.decode.read

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse

interface PixReadQRCodeContract {

    interface View : BaseView {
        fun onReadQRCode(qrcode: String)
        fun onSuccessValidateQRCode(qrCodeDecode: QRCodeDecodeResponse)
    }

    interface Presenter {
        fun onValidateQRCode(qrcode: String)
        fun isFirstTimeAskCameraPermission(): Boolean
        fun onUpdateAskCameraPermission()
        fun onResume()
        fun onPause()
    }
}