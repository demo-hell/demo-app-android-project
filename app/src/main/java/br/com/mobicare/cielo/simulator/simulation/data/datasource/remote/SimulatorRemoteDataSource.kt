package br.com.mobicare.cielo.simulator.simulation.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.simulator.simulation.data.model.toSimulation
import br.com.mobicare.cielo.simulator.simulation.data.model.toSimulatorProducts
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts

class SimulatorRemoteDataSource(
    private val serverApi: SimulatorApi,
    private val safeApiCaller: SafeApiCaller
) {
    private val errorDefault =
        CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    suspend fun getSimulationProducts(): CieloDataResult<SimulatorProducts> {
        var result: CieloDataResult<SimulatorProducts> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getSimulationProducts()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    response.toSimulatorProducts()
                )
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getSimulation(
        productTypeCode: Int?,
        fastReceiveIndicator: Boolean?,
        installmentAmount: Int?,
        salesValue: Double?
    ): CieloDataResult<List<Simulation>> {
        var result: CieloDataResult<List<Simulation>> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getSimulation(
                productTypeCode = productTypeCode,
                fastReceiveIndicator = fastReceiveIndicator,
                installmentAmount = installmentAmount,
                salesValue = salesValue
            )
        }.onSuccess { listResponse ->
            result = listResponse.body()?.let { response ->
                CieloDataResult.Success(response.map { it.toSimulation() })
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }
}