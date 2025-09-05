package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.domain.repository.DetainerRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository

class GetDetainerUseCase(private val repository: DetainerRemoteRepository) {
    suspend operator fun invoke(consentId: String): CieloDataResult<DetainerResponse> {
        return repository.getDetainer(consentId)
    }
}