package br.com.mobicare.cielo.newRecebaRapido.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticEligibilityRepository

class GetReceiveAutomaticEligibilityUseCase(
    private val repository: ReceiveAutomaticEligibilityRepository,
) {
    suspend operator fun invoke(): CieloDataResult<ReceiveAutomaticEligibility> = repository.getReceiveAutomaticEligibility()
}
