package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository

class GetPixEligibilityInfringementUseCase(
    private val repository: PixInfringementRepository
) {

    suspend operator fun invoke(idEndToEnd: String): CieloDataResult<PixEligibilityInfringementResponse> {
        return repository.getInfringement(idEndToEnd)
    }

}