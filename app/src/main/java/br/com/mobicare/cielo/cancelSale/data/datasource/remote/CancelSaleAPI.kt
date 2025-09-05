package br.com.mobicare.cielo.cancelSale.data.datasource.remote

import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.data.model.response.BalanceInquiryResponse
import br.com.mobicare.cielo.cancelSale.data.model.response.CancelSaleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CancelSaleAPI {

    @GET("site-cielo/v1/sales/refunds/eligibles")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun balanceInquiry(
        @Query("cardBrandCode") cardBrandCode: String,
        @Query("authorizationCode") authorizationCode: String,
        @Query("nsu") nsu: String,
        @Query("truncatedCardNumber") truncatedCardNumber: String,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,
        @Query("paymentType") paymentType: String,
        @Query("grossAmount") grossAmount: String,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Response<BalanceInquiryResponse>

    @POST("site-cielo/v1/sales/refunds")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun sendSaleToCancel(
        @Header("otpCode") otpGenerated: String? = null,
        @Body sales: ArrayList<CancelSaleRequest>,
    ): Response<CancelSaleResponse>
}