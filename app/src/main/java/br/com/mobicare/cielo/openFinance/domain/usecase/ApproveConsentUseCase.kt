package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.domain.repository.ApproveConsentRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository

class ApproveConsentUseCase(private val repository: ApproveConsentRemoteRepository) {
    suspend operator fun invoke(consentId: ConsentIdRequest, token:String): CieloDataResult<ConsentResponse> {
        return repository.approveConsent(consentId, token)
    }
}