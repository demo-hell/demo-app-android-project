package br.com.mobicare.cielo.turboRegistration.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.dataSource.EligibilityDataSource
import br.com.mobicare.cielo.turboRegistration.data.model.response.EligibilityResponse
import br.com.mobicare.cielo.turboRegistration.domain.repository.EligibilityRepository

class EligibilityRepositoryImpl(
    private val dataSource: EligibilityDataSource
) : EligibilityRepository {
    override suspend fun getEligibility(): CieloDataResult<EligibilityResponse> =
        dataSource.getRegistrationEligibility()
}