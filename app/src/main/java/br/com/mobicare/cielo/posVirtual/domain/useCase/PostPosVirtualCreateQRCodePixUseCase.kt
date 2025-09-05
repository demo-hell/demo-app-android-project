package br.com.mobicare.cielo.posVirtual.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualQRCodePixRepository

class PostPosVirtualCreateQRCodePixUseCase(
    private val repository: PosVirtualQRCodePixRepository
) {

    suspend operator fun invoke(
        otpCode: String,
        request: PosVirtualCreateQRCodeRequest
    ): CieloDataResult<PosVirtualCreateQRCodeResponse> =
        repository.postPosVirtualCreateQRCodePix(otpCode, request)

}