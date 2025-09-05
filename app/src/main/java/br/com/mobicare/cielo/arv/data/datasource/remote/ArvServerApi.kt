package br.com.mobicare.cielo.arv.data.datasource.remote

import br.com.cielo.libflue.util.ONE_HUNDRED
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.response.*
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import retrofit2.Response
import retrofit2.http.*

interface ArvServerApi {

    @GET("site-cielo/v1/anticipation")
    @Headers("auth: required", "accessToken: required")
    suspend fun getArvAnticipation(
        @Query("negotiationType") negotiationType: String? = null,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null,
        @Query("cardBrandCode") cardBrandCode: List<Int>? = null,
        @Query("acquirerCode") acquirerCode: List<Int>? = null,
        @Query("amount") amount: Double? = null,
        @Query("receiveToday") receiveToday: Boolean? = null
    ): Response<ArvAnticipationResponse>

    @GET("site-cielo/v1/anticipation/negotiations")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getNegotiations(
        @Query("negotiationType") negotiationType: String? = null,
        @Query("status") status: String? = null,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null,
        @Query("page") page: Int = ONE_HUNDRED,
        @Query("pageSize") pageSize: Int = TWENTY_FIVE,
        @Query("modalityType") modalityType: String?,
        @Query("operationNumber") operationNumber: String?
    ): Response<ArvHistoricResponse>

    @GET("site-cielo/v1/anticipation/banks")
    @Headers("auth: required", "accessToken: required")
    suspend fun getBanks(): Response<List<ArvBankResponse>>

    @POST("site-cielo/v1/anticipation")
    @Headers("auth: required", "accessToken: required")
    suspend fun confirmArvAnticipation(
        @Body body: ArvConfirmAnticipationRequest
    ): Response<ArvConfirmAnticipationResponse>

    @GET("site-cielo/v1/anticipation/schedule")
    @Headers("auth: required", "accessToken: required")
    suspend fun getArvScheduledAnticipation(): Response<ArvScheduledAnticipationResponse>

    @POST("site-cielo/v1/anticipation/schedule")
    @Headers("auth: required", "accessToken: required")
    suspend fun confirmArvScheduledAnticipation(
        @Body body: ArvConfirmScheduledAnticipationRequest
    ): Response<Void>

    @DELETE("site-cielo/v1/anticipation/schedule/{negotiationType}")
    @Headers("auth: required", "accessToken: required")
    suspend fun cancelArvScheduledAnticipation(
        @Path("negotiationType") negotiationType: String
    ): Response<Void>

    @GET("site-cielo/v1/merchant/permissions/eligible")
    @Headers("auth: required", "accessToken: required")
    suspend fun getOptInStatus(): Response<ArvOptInResponse>

    @GET("site-cielo/v1/anticipation/schedule/contract/{negotiationType}")
    @Headers("auth: required", "accessToken: required")
    suspend fun getArvScheduledContract(
        @Path("negotiationType") negotiationType: String
    ): Response<ArvScheduleContractResponse>

    @GET("site-cielo/v1/anticipation/schedule/contract/detail")
    @Headers("auth: required", "accessToken: required")
    suspend fun getBranchesContracts(): Response<ArvBranchContractResponse>

}