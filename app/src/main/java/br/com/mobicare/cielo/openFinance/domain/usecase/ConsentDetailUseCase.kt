package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.repository.ConsentDetailRemoteRepository

class ConsentDetailUseCase(private val repository: ConsentDetailRemoteRepository) {
    suspend operator fun invoke(
        consentId: String
    ): CieloDataResult<ConsentDetail> {
        return repository.getConsentDetail(consentId)
    }
}