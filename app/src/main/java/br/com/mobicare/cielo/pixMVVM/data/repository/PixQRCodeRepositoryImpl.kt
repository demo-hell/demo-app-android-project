package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixQRCodeRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixQRCodeRepository

class PixQRCodeRepositoryImpl(
    private val dataSource: PixQRCodeRemoteDataSource,
) : PixQRCodeRepository {
    override suspend fun postDecodeQRCode(request: PixDecodeQRCodeRequest): CieloDataResult<PixDecodeQRCode> =
        dataSource.postDecodeQRCode(request)
}
