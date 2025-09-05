package br.com.mobicare.cielo.tapOnPhone.data.api

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse
import br.com.mobicare.cielo.tapOnPhone.domain.model.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface TapOnPhoneAPI {

    //https://digitalti.hdevelo.com.br/taponphone/swagger.json

    @GET("site-cielo/v1/taponphone/pos-virtual/whitelist")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getPosVirtualWhiteList(): Observable<PosVirtualWhiteListResponse>

    @GET("site-cielo/v1/merchant/solutions/brands")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun loadBrands(): Observable<List<Solution>>

    @GET("site-cielo/v1/taponphone/pos-virtual/offers")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getTapOnPhoneOffer(
        @Query("additionalProduct") additionalProduct: String? = null
    ): Observable<OfferResponse>

    @POST("site-cielo/v1/taponphone/pos-virtual/orders")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun requestTapOnPhoneOrder(@Body request: OrdersRequest): Observable<OrdersResponse>

    @GET("site-cielo/v1/taponphone/fraud-analysis/session")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getTapOnPhoneSessionId(): Observable<TapOnPhoneSessionIdResponse>

    @GET("site-cielo/v1/taponphone/eligibility")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getEligibilityStatus(): Observable<TapOnPhoneEligibilityResponse>

    @POST("site-cielo/v1/taponphone/device/activate")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun activateTerminal(
        @Body request: TapOnPhoneTerminalRequest
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/taponphone/device")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun createTerminal(@Body request: TapOnPhoneTerminalRequest): Observable<TapOnPhoneTerminalResponse>

    @POST("site-cielo/v1/taponphone/device/token")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getTerminalInfo(
        @Header("otpCode") otpCode: String,
        @Body request: TapOnPhoneTerminalRequest
    ): Observable<TapOnPhoneTerminalResponse>

}