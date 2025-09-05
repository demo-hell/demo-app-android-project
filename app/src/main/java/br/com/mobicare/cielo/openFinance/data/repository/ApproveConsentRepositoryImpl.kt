package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ApproveConsentDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.domain.repository.ApproveConsentRemoteRepository

class ApproveConsentRepositoryImpl(
    private val dataSource: ApproveConsentDataSource
):ApproveConsentRemoteRepository {
    override suspend fun approveConsent(consentId: ConsentIdRequest, token:String):
            CieloDataResult<ConsentResponse> {
        return dataSource.approveConsent(consentId, token)

    }
}