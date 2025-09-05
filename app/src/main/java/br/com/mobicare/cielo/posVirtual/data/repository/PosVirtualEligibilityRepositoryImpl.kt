package br.com.mobicare.cielo.posVirtual.data.repository

import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualEligibilityDataSource
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualEligibilityRepository

class PosVirtualEligibilityRepositoryImpl(
    private val dataSource: PosVirtualEligibilityDataSource
) : PosVirtualEligibilityRepository {

    override suspend fun getEligibility() = dataSource.getEligibility()

}