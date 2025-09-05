package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import br.com.mobicare.cielo.turboRegistration.domain.repository.BusinessSectorRepository

class SearchBusinessLinesUseCase(private val businessSectorRepository: BusinessSectorRepository) {
    suspend operator fun invoke(searchQuery: String?): CieloDataResult<List<Mcc>> = businessSectorRepository.searchBusinessLine(searchQuery)
}