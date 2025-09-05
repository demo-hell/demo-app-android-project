package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare

interface ConfirmShareRemoteRepository {
    suspend fun confirmShare(confirmShareRequest: ConfirmShareRequest): CieloDataResult<ConfirmShare>
}