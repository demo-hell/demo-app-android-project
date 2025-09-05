package br.com.mobicare.cielo.solesp.api

import br.com.mobicare.cielo.solesp.domain.SolespRequest
import br.com.mobicare.cielo.solesp.domain.SolespResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SolespAPI {

    @POST("site-cielo/v1/statement/solesp")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun sendSolespRequest(
        @Body solespRequest: SolespRequest?
    ): Observable<SolespResponse>

}