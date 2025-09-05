package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixReceiptsScheduledRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixExtractRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixExtractRepository

class PixExtractRepositoryImpl(
    private val remoteDataSource: PixExtractRemoteDataSource,
) : PixExtractRepository {
    override suspend fun getExtract(request: PixExtractFilterRequest): CieloDataResult<PixExtract> = remoteDataSource.getExtract(request)

    override suspend fun getReceiptsScheduled(request: PixReceiptsScheduledRequest): CieloDataResult<PixReceiptsScheduled> =
        remoteDataSource.getReceiptsScheduled(request)
}
