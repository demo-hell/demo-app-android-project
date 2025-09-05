package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse

interface SharedDataConsentsRemoteRepository {
    suspend fun getConsents(
        journey: String,
        page: String,
        pageSize: String?
    ): CieloDataResult<SharedDataConsentsResponse>
}