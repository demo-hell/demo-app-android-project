package br.com.mobicare.cielo.eventTracking.data.datasource.api

import br.com.mobicare.cielo.eventTracking.data.model.response.EventRequestResponse
import br.com.mobicare.cielo.eventTracking.data.model.response.MachineRequestResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface TrackEventApi {
    @GET("site-cielo/v1/merchant/solutions/events/delivery")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getDeliveryEventList(
        @Query("initialDate") initialDate: String?, @Query("endDate") endDate: String?, @Query("serviceType") serviceType: String?
    ): MachineRequestResponse

    @GET("site-cielo/v1/merchant/solutions/events/all")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAllCalls(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
    ) : EventRequestResponse
}