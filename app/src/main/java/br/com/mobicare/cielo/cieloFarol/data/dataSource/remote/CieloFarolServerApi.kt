package br.com.mobicare.cielo.cieloFarol.data.dataSource.remote

import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CieloFarolServerApi {

    @GET("site-cielo/v1/farol/indicators/consolidated-pills")
    suspend fun getCieloFarol(
            @Header("authorization") authorization: String,
            @Query("merchant") merchant: String?
    ): Response<CieloFarolResponse>
}