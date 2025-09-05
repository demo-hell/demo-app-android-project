package br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse

interface PixCopyPasteQRCodeContract {
    interface View : BaseView {
        fun onSuccessValidateQRCode(qrCodeDecode: QRCodeDecodeResponse?)
    }
    interface Presenter {
        fun onValidateQRCode(qrcode: String)
        fun onResume()
        fun onPause()
    }
}