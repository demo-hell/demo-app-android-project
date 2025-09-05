package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail

interface ConsentDetailRemoteRepository {
    suspend fun getConsentDetail(
        consentId: String
    ): CieloDataResult<ConsentDetail>
}