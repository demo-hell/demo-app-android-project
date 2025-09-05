package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse

interface ApproveConsentRemoteRepository {
    suspend fun approveConsent(
        consentId: ConsentIdRequest,
        token: String
    ): CieloDataResult<ConsentResponse>
}