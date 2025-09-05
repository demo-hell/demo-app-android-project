package br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.remote

import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface InteractBannerServerAPI {

    @GET("site-cielo/v1/merchant/offers")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getHiringOffers(): Response<List<HiringOffers>>
}