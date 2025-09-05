package br.com.mobicare.cielo.pixMVVM.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode

interface PixQRCodeRemoteDataSource {
    suspend fun postDecodeQRCode(request: PixDecodeQRCodeRequest): CieloDataResult<PixDecodeQRCode>
}
