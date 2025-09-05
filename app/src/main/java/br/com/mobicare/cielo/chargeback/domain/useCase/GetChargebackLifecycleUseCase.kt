package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository

class GetChargebackLifecycleUseCase(
    private val repository: ChargebackRepository
) {
    suspend operator fun invoke(caseId: Int) = repository.getChargebackLifecycle(caseId)
}