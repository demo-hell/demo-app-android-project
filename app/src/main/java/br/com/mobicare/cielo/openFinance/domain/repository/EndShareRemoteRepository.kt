package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest

interface EndShareRemoteRepository {
    suspend fun endShare(
        otpCode: String,
        request: EndShareRequest
    ): CieloDataResult<Any>
}