package br.com.mobicare.cielo.technicalSupport.domain.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse

interface PredictiveBatteryDataSource {

    suspend fun postChangeBattery(request: BatteryRequest): CieloDataResult<BatteryResponse>

}