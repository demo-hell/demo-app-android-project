package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixProfileRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixProfileRepository

class PixProfileRepositoryImpl(
    private val remoteDataSource: PixProfileRemoteDataSource,
) : PixProfileRepository {

    override suspend fun update(otpCode: String?, request: PixProfileRequest): CieloDataResult<String> {
        return remoteDataSource.update(otpCode, request)
    }

}