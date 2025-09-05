package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.RejectConsentDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.domain.repository.RejectConsentRemoteRepository

class RejectConsentRepositoryImpl(
    private val dataSource: RejectConsentDataSource
) : RejectConsentRemoteRepository {
    override suspend fun rejectConsent(
        rejectConsentRequest: RejectConsentRequest
    ): CieloDataResult<ConsentResponse> {
        return dataSource.rejectConsent(rejectConsentRequest)
    }
}