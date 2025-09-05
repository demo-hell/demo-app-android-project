package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.BusinessSectorRepository

class UpdateBusinessSectorUseCase(private val businessSectorRepository: BusinessSectorRepository) {
    suspend operator fun invoke(businessUpdateRequest: BusinessUpdateRequest): CieloDataResult<Void> =
        businessSectorRepository.updateBusinessSector(businessUpdateRequest)
}