package br.com.mobicare.cielo.posVirtual.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse

interface PosVirtualQRCodePixRepository {

    suspend fun postPosVirtualCreateQRCodePix(
        otpCode: String,
        request: PosVirtualCreateQRCodeRequest
    ): CieloDataResult<PosVirtualCreateQRCodeResponse>

}