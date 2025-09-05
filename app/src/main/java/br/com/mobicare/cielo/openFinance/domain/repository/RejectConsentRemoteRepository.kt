package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse

interface RejectConsentRemoteRepository {
    suspend fun rejectConsent(
        rejectConsentRequest: RejectConsentRequest
    ): CieloDataResult<ConsentResponse>
}