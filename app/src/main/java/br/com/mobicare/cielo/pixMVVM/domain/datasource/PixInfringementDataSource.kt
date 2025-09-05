package br.com.mobicare.cielo.pixMVVM.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse

interface PixInfringementDataSource {

    suspend fun getInfringement(idEndToEnd: String): CieloDataResult<PixEligibilityInfringementResponse>

    suspend fun postInfringement(request: PixCreateNotifyInfringementRequest): CieloDataResult<PixCreateNotifyInfringementResponse>

}