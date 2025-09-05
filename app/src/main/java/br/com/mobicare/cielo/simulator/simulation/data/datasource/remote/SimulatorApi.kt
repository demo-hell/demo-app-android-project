package br.com.mobicare.cielo.simulator.simulation.data.datasource.remote

import br.com.mobicare.cielo.simulator.simulation.data.model.SimulationResponse
import br.com.mobicare.cielo.simulator.simulation.data.model.SimulatorProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SimulatorApi {
    @GET("site-cielo/v1/merchant/products/sales-simulator/products")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getSimulationProducts(): Response<SimulatorProductsResponse>

    @GET("site-cielo/v1/merchant/products/sales-simulator")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getSimulation(
        @Query("productTypeCode") productTypeCode: Int?,
        @Query("fastReceiveIndicator") fastReceiveIndicator: Boolean?,
        @Query("taxTransferIndicator") taxTransferIndicator: Boolean? = null,
        @Query("installmentAmount") installmentAmount: Int?,
        @Query("salesValue") salesValue: Double?,
    ): Response<List<SimulationResponse>>
}