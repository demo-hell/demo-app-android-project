package br.com.mobicare.cielo.contactCielo.data.datasource.remote

import br.com.mobicare.cielo.contactCielo.data.model.response.SegmentCodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
interface SegmentCodeServerApi {

    @GET("site-cielo/v1/merchant")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getSegmentCode(): Response<SegmentCodeResponse>
}