package br.com.mobicare.cielo.technicalSupport.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.technicalSupport.data.dataSource.remote.TechnicalSupportAPI
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse
import br.com.mobicare.cielo.technicalSupport.domain.dataSource.PredictiveBatteryDataSource

class PredictiveDateSourceImpl(
    private val serverAPI: TechnicalSupportAPI,
    private val safeApiCaller: SafeApiCaller
) : PredictiveBatteryDataSource {

    override suspend fun postChangeBattery(request: BatteryRequest): CieloDataResult<BatteryResponse> {
        lateinit var result: CieloDataResult<BatteryResponse>

        val apiResult = safeApiCaller.safeApiCall {
            serverAPI.postChangeBattery(request)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { CieloDataResult.Success(it) } ?: CieloDataResult.Empty()
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}