package br.com.mobicare.cielo.cancelSale.data.repository

import br.com.mobicare.cielo.cancelSale.data.datasource.CancelSaleDataSource
import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.cancelSale.domain.repository.CancelSaleRemoteRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class CancelSaleRepositoryImpl(private val dataSource: CancelSaleDataSource) :
    CancelSaleRemoteRepository {
    override suspend fun cancelSale(
        otpCode: String,
        sales: ArrayList<CancelSaleRequest>
    ): CieloDataResult<CancelSale> {
        return dataSource.cancelSale(otpCode, sales)
    }
}