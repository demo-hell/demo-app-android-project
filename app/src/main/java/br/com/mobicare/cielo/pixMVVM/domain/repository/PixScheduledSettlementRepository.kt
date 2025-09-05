package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse

interface PixScheduledSettlementRepository {
    suspend fun create(otpCode: String?, request: PixScheduledSettlementRequest): CieloDataResult<PixScheduledSettlementResponse>
    suspend fun update(otpCode: String?, request: PixScheduledSettlementRequest): CieloDataResult<PixScheduledSettlementResponse>
}