package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.response.EligibilityResponse
import br.com.mobicare.cielo.turboRegistration.domain.repository.EligibilityRepository

class GetEligibilityUseCase(private val repository: EligibilityRepository) {
    suspend operator fun invoke(): CieloDataResult<EligibilityResponse> = repository.getEligibility()
}