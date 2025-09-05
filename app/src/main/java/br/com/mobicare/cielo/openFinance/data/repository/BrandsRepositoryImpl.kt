package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.BrandsDataSource
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.repository.BrandsRemoteRepository

class BrandsRepositoryImpl(private val dataSource: BrandsDataSource) : BrandsRemoteRepository {
    override suspend fun getBrands(name: String): CieloDataResult<List<Brand>> {
        return dataSource.getBrands(name)
    }
}