package br.com.mobicare.cielo.component.impersonate.data.datasource.remote

import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse
import retrofit2.Response
import retrofit2.http.*

interface ImpersonateServiceAPI {

    @POST("site-cielo/v1/merchant/impersonate/{ec}")
    @Headers(value = ["accessToken: required"])
    suspend fun postImpersonate(
        @Path("ec") ec: String,
        @Query("type") type: String,
        @Body fingerprint: ImpersonateRequest
    ): Response<ImpersonateResponse>

}