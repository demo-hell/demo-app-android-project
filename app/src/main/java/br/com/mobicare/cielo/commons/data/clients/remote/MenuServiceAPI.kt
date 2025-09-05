package br.com.mobicare.cielo.commons.data.clients.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.ANDROID
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MenuServiceAPI {

    @GET("/site-cielo/v1/menu/app")
    @Headers("auth: required", "accessToken: required")
    suspend fun getMenu(
        @Query("platform") platform: String = ANDROID
    ): Response<AppMenuResponse>

    @GET("site-cielo/v1/taponphone/pos-virtual/whitelist")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getPosVirtualWhiteList(): Response<PosVirtualWhiteListResponse>

}