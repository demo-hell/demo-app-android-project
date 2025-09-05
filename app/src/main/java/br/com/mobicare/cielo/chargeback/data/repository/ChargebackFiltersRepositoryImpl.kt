package br.com.mobicare.cielo.chargeback.data.repository

import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteFiltersDataSource
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackFiltersRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ChargebackFiltersRepositoryImpl(private val dataSource: ChargebackRemoteFiltersDataSource):
    ChargebackFiltersRepository {


    override suspend fun getChargebackFilters(): CieloDataResult<ChargebackFilters> {
        return dataSource.getChargebackFilters()
    }


}