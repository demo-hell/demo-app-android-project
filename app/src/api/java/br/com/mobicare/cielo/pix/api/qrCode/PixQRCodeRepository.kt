package br.com.mobicare.cielo.pix.api.qrCode

import br.com.mobicare.cielo.pix.domain.QRCodeChargeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest

class PixQRCodeRepository(private val dataSource: PixQRCodeDataSource) :
    PixQRCodeRepositoryContract {

    override fun chargeQRCode(
        otpCode: String,
        body: QRCodeChargeRequest?
    ) = dataSource.chargeQRCode(otpCode, body)

    override fun decodeQRCode(body: QRCodeDecodeRequest) =
        dataSource.decodeQRCode(body)
}