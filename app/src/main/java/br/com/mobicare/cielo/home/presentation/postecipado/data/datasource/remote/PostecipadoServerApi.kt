package br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.remote

import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface PostecipadoServerApi {
    @GET("/site-cielo/v1/merchant/solutions/plans/{planName}")
    @Headers(value = ["accessToken: required"])
    suspend fun getPlanInformation(
        @Path("planName") planName: String
    ): Response<PlanInformationResponse>
}