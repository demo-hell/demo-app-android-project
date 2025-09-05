package br.com.mobicare.cielo.posVirtual.data.dataSource.remote

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualBrandsResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateOrderResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualResponse
import retrofit2.Response
import retrofit2.http.*

interface PosVirtualAPI {

    @GET("site-cielo/v1/taponphone/pos-virtual")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getEligibility(): Response<PosVirtualResponse>

    @GET("site-cielo/v1/taponphone/pos-virtual/offers")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getOffer(
        @Query("additionalProduct") additionalProduct: String? = null
    ): Response<OfferResponse>

    @GET("site-cielo/v1/merchant/solutions/brands")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getBrands(): Response<List<PosVirtualBrandsResponse>>

    @POST("site-cielo/v1/taponphone/pos-virtual/orders")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postCreateOrder(
        @Header("otpCode") otpCode: String,
        @Body body: OrdersRequest
    ): Response<PosVirtualCreateOrderResponse>

    @POST("site-cielo/v1/pix/transactions/qrcode")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postCreateQRCodePix(
        @Header("otpCode") otpCode: String,
        @Body body: PosVirtualCreateQRCodeRequest,
    ): Response<PosVirtualCreateQRCodeResponse>

}