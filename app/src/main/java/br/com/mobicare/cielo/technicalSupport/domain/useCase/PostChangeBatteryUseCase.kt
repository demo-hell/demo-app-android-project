package br.com.mobicare.cielo.technicalSupport.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse
import br.com.mobicare.cielo.technicalSupport.domain.repository.PredictiveBatteryRepository

class PostChangeBatteryUseCase(
    private val repository: PredictiveBatteryRepository
) {

    suspend operator fun invoke(request: BatteryRequest): CieloDataResult<BatteryResponse> {
        return repository.postChangeBattery(request)
    }

}