package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare

interface GivenUpShareRemoteRepository {
    suspend fun givenUpShare(givenUpShareRequest: GivenUpShareRequest): CieloDataResult<GivenUpShare>
}