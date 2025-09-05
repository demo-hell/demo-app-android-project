package br.com.mobicare.cielo.cieloFarol.data.repository

import br.com.mobicare.cielo.cieloFarol.data.dataSource.CieloFarolDataSource
import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import br.com.mobicare.cielo.cieloFarol.domain.repository.CieloFarolRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class CieloFarolRepositoryImpl(private val dataSource: CieloFarolDataSource) :
    CieloFarolRepository {

    override suspend fun getCieloFarol(authorization: String, merchant: String?): CieloDataResult<CieloFarolResponse> =
            dataSource.getCieloFarol(authorization, merchant)
}