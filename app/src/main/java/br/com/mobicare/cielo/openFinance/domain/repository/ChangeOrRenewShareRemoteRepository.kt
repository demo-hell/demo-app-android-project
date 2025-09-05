package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare

interface ChangeOrRenewShareRemoteRepository {
    suspend fun changeOrRenewShare(
        changeOrRenewShareRequest: ChangeOrRenewShareRequest
    ): CieloDataResult<ChangeOrRenewShare>
}