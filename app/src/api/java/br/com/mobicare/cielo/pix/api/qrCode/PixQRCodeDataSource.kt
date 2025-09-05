package br.com.mobicare.cielo.pix.api.qrCode

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.QRCodeChargeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest

class PixQRCodeDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun chargeQRCode(
        otpCode: String,
        body: QRCodeChargeRequest?
    ) = api.chargeQRCode(authorization, otpCode, body)

    fun decodeQRCode(body: QRCodeDecodeRequest) =
        api.decodeQRCode(authorization, body)
}