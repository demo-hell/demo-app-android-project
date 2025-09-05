package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare

interface CreateShareRemoteRepository {
    suspend fun createShare(request: CreateShareRequest) : CieloDataResult<CreateShare>
}