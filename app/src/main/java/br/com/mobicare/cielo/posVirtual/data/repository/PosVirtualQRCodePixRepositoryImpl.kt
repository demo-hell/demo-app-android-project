package br.com.mobicare.cielo.posVirtual.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualQRCodePixDataSource
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualQRCodePixRepository

class PosVirtualQRCodePixRepositoryImpl(
    private val dataSource: PosVirtualQRCodePixDataSource
) : PosVirtualQRCodePixRepository {

    override suspend fun postPosVirtualCreateQRCodePix(
        otpCode: String,
        request: PosVirtualCreateQRCodeRequest
    ): CieloDataResult<PosVirtualCreateQRCodeResponse> =
        dataSource.postPosVirtualCreateQRCodePix(otpCode, request)

}