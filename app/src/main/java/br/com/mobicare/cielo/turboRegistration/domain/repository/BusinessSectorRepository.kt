package br.com.mobicare.cielo.turboRegistration.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc

interface BusinessSectorRepository {
    suspend fun searchBusinessLine(searchQuery: String?): CieloDataResult<List<Mcc>>
    suspend fun updateBusinessSector(businessSector: BusinessUpdateRequest): CieloDataResult<Void>
}