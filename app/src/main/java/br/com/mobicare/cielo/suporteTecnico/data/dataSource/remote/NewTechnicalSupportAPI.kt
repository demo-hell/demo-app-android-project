package br.com.mobicare.cielo.suporteTecnico.data.dataSource.remote

import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.suporteTecnico.data.EquipmentEligibilityResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments
import br.com.mobicare.cielo.suporteTecnico.data.ScheduleDataResponse
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface NewTechnicalSupportAPI {

    @GET("site-cielo/v1/merchant")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getMerchant(): Response<UserOwnerSupportResponse>

    @GET("site-cielo/v1/merchant/solutions/equipments")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun merchantSolutionsEquipments(): Response<TerminalsResponse>

    @GET("site-cielo/v1/merchant/solutions/orders/availability")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getScheduleAvailability(): Response<ScheduleDataResponse>

    @GET("site-cielo/v1/merchant/solutions/orders/occurrences")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getProblemEquipments(): Response<List<ProblemEquipments>>

    @GET("site-cielo/v1/merchant/solutions/orders/eligibility")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getEligibility(
        @Query("technology") technology: String,
        @Query("code") code: String
    ): Response<EquipmentEligibilityResponse>

    @POST("site-cielo/v1/merchant/solutions/orders/replacements")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postOrdersReplacements(
        @Body orderRequest: OpenTicket
    ): Response<OrderReplacementResponse>
}