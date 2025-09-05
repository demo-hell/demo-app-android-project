package br.com.mobicare.cielo.pix.api.qrCode

import br.com.mobicare.cielo.pix.domain.QRCodeChargeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeChargeResponse
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import io.reactivex.Observable

interface PixQRCodeRepositoryContract {
    fun chargeQRCode(
        otpCode: String,
        body: QRCodeChargeRequest?
    ): Observable<QRCodeChargeResponse>

    fun decodeQRCode(
        body: QRCodeDecodeRequest
    ): Observable<QRCodeDecodeResponse>
}