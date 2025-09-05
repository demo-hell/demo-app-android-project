package br.com.mobicare.cielo.pix.ui.qrCode.charge.generate

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.domain.QRCodeChargeResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse

interface PixGenerateQRCodeContract {

    interface View : BaseView {
        fun onShowData(
            amount: Double?,
            message: String?,
            expirationDate: String?,
            identifier: String?
        )

        fun onSuccessGenerateQRCode(response: QRCodeChargeResponse)
        fun onErrorGenerateQRCode(onGenericError: () -> Unit) }

    interface Presenter {
        fun getUsername(): String
        fun onValidateKey(myKeys: List<PixKeysResponse.KeyItem>?): String?
        fun getFirstActiveKey(keys: List<PixKeysResponse.KeyItem>): PixKeysResponse.KeyItem
        fun onGetData()
        fun onSaveData(
            amount: Double?,
            message: String?,
            expirationDate: String?,
            identifier: String?
        )

        fun onGenerateQRCode(
            amount: Double?,
            message: String?,
            expirationDate: String?,
            identifier: String?,
            otp: String
        )

        fun onResume()
        fun onPause()
    }
}