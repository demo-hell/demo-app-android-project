package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.TermsOfUseDataSource
import br.com.mobicare.cielo.openFinance.domain.model.TermsOfUse
import br.com.mobicare.cielo.openFinance.domain.repository.TermsOfUseRemoteRepository

class TermsOfUseRepositoryImpl(private val dataSource: TermsOfUseDataSource) :
    TermsOfUseRemoteRepository {
    override suspend fun getTermsOfUse(): CieloDataResult<TermsOfUse> {
        return dataSource.getTermsOfUse()
    }
}