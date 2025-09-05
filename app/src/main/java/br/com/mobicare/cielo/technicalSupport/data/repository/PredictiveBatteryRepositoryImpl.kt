package br.com.mobicare.cielo.technicalSupport.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse
import br.com.mobicare.cielo.technicalSupport.domain.dataSource.PredictiveBatteryDataSource
import br.com.mobicare.cielo.technicalSupport.domain.repository.PredictiveBatteryRepository

class PredictiveBatteryRepositoryImpl(
    private val dataSource: PredictiveBatteryDataSource
) : PredictiveBatteryRepository{

    override suspend fun postChangeBattery(request: BatteryRequest): CieloDataResult<BatteryResponse> {
        return dataSource.postChangeBattery(request)
    }

}