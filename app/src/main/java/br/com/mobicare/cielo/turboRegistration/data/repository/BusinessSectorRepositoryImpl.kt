package br.com.mobicare.cielo.turboRegistration.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.dataSource.BusinessSectorDataSource
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import br.com.mobicare.cielo.turboRegistration.domain.repository.BusinessSectorRepository

class BusinessSectorRepositoryImpl(
    private val businessSectorDataSource: BusinessSectorDataSource
) : BusinessSectorRepository {
    override suspend fun searchBusinessLine(searchQuery: String?): CieloDataResult<List<Mcc>> {
        return businessSectorDataSource.getAllBusinessLine(searchQuery)
    }

    override suspend fun updateBusinessSector(
        businessSector: BusinessUpdateRequest): CieloDataResult<Void> =
        businessSectorDataSource.updateBusinessSector(businessSector)
}