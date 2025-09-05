package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ConsentDetailDataSource
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.repository.ConsentDetailRemoteRepository

class ConsentDetailRepositoryImpl(private val dataSource: ConsentDetailDataSource) :
    ConsentDetailRemoteRepository {
    override suspend fun getConsentDetail(
        consentId: String,
    ): CieloDataResult<ConsentDetail> {
        return dataSource.getConsentDetail(consentId)
    }
}