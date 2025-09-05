package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.PixMerchantDataSource
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository

class PixMerchantRemoteRepositoryImpl(
    private val dataSource: PixMerchantDataSource
) : PixMerchantRemoteRepository {

    override suspend fun getPixMerchantListOpenFinance():
            CieloDataResult<List<PixMerchantListResponse>> {
        return dataSource.getPixMerchantListOpenFinance()
    }
}