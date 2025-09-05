package br.com.mobicare.cielo.posVirtual.domain.useCase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualEligibilityRepository

class GetPosVirtualEligibilityUseCase(
    private val repository: PosVirtualEligibilityRepository
) : UseCaseWithoutParams<PosVirtual> {

    override suspend operator fun invoke() = repository.getEligibility()

}