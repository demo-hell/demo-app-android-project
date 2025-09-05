package br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote

import br.com.mobicare.cielo.newRecebaRapido.data.model.OfferResponse
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticEligibilityResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ReceiveAutomaticApi {
    @GET("site-cielo/v1/merchant/offers/custom-fast-repay")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getReceiveAutomaticOffers(
        @Query("periodicity") periodicity: String?,
        @Query("nextValidityPeriod") nextValidityPeriod: Boolean?
    ): Response<List<OfferResponse>>

    @POST("site-cielo/v1/receba-rapido/custom")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun contractReceiveAutomaticOffer(
        @Body body: ReceiveAutomaticContractRequest
    ) : Response<Void>

    @GET("/site-cielo/v1/receba-rapido/eligibility")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getReceiveAutomaticEligibility(): Response<ReceiveAutomaticEligibilityResponse>
}