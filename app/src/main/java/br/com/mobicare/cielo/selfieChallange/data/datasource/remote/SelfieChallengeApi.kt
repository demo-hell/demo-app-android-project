package br.com.mobicare.cielo.selfieChallange.data.datasource.remote

import br.com.mobicare.cielo.selfieChallange.data.model.request.SelfieChallengeRequest
import br.com.mobicare.cielo.selfieChallange.data.model.response.SelfieChallengeResponse
import br.com.mobicare.cielo.selfieChallange.data.model.response.StoneAgeTokenResponse
import retrofit2.Response
import retrofit2.http.*

interface SelfieChallengeApi{
    @GET("site-cielo/v1/authorities/document-scan/signed-token")
    suspend fun getStoneAgeToken(): Response<StoneAgeTokenResponse>

    @POST("site-cielo/v1/accounts/face-id/token")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postSelfieChallenge(
        @Query("operation") operation: String,
        @Body body: SelfieChallengeRequest
    ): Response<SelfieChallengeResponse>

    @POST("site-cielo/v1/accounts/face-id/token/{username}")
    @Headers(value = ["auth: no-required", "accessToken: no-required", "ec: no-required"])
    suspend fun postSelfieChallenge(
        @Path("username") userName: String,
        @Query("operation") operation: String,
        @Body body: SelfieChallengeRequest
    ): Response<SelfieChallengeResponse>
}