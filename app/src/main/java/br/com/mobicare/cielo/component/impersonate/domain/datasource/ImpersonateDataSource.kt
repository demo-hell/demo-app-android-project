package br.com.mobicare.cielo.component.impersonate.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse

interface ImpersonateDataSource {

    suspend fun postImpersonate(
        ec: String,
        type: String,
        impersonateRequest: ImpersonateRequest
    ): CieloDataResult<ImpersonateResponse>

}