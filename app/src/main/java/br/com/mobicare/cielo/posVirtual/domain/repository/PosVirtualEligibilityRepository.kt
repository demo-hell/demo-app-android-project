package br.com.mobicare.cielo.posVirtual.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual

interface PosVirtualEligibilityRepository {

    suspend fun getEligibility(): CieloDataResult<PosVirtual>

}