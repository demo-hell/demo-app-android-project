package br.com.mobicare.cielo.newRecebaRapido.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility

interface ReceiveAutomaticEligibilityRepository {
    suspend fun getReceiveAutomaticEligibility(): CieloDataResult<ReceiveAutomaticEligibility>
}
