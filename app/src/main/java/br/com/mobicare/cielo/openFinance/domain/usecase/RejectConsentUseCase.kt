package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.domain.repository.RejectConsentRemoteRepository

class RejectConsentUseCase(
    private val repository: RejectConsentRemoteRepository
) {
    suspend operator fun invoke(rejectConsentRequest: RejectConsentRequest): CieloDataResult<ConsentResponse> {
        return repository.rejectConsent(rejectConsentRequest)
    }
}