package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare

interface UpdateShareRemoteRepository {
    suspend fun updateShare(
        shareId: String,
        request: UpdateShareRequest
    ) : CieloDataResult<UpdateShare>
}