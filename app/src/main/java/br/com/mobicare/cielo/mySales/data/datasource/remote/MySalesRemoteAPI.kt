package br.com.mobicare.cielo.mySales.data.datasource.remote

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummarySalesHistory
import br.com.mobicare.cielo.mySales.data.model.responses.SummarySalesResponse
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface MySalesRemoteAPI {

    @GET("site-cielo/v1/sales")
    suspend fun getSummarySales(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("initialAmount") initialAmount: Double?,
        @Query("finalAmount") finalAmount: Double?,
        @Query("customId") customId: String?,
        @Query("saleCode") saleCode: String?,
        @Query("truncatedCardNumber") truncatedCardNumber: String?,
        @Query("cardBrands") cardBrands: List<Int>?,
        @Query("paymentTypes") paymentTypes: List<Int>?,
        @Query("terminal") terminal: List<String>?,
        @Query("status") status: List<Int>?,
        @Query("cardNumber") cardNumber: Int?,
        @Query("nsu") nsu: String?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("transactionId") tid: String?,
        @Query("roNumber") roNumber: String?
    ): Response<SummarySalesResponse>

    @GET("site-cielo/v1/sales/postings")
    suspend fun getSummarySalesOnline(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("cardBrand") cardBrand: List<Int>?,
        @Query("paymentType") paymentType: List<Int>?,
        @Query("terminal") terminal: List<String>?,
        @Query("status") status: List<Int>?,
        @Query("cardNumber") cardNumber: Int?,
        @Query("nsu") nsu: String?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("id") page: String?,
        @Query("pageSize") pageSize: Int?
    ): Response<SummarySalesResponse?>


    @GET("/site-cielo/v1/sales/refunds/lifecycle")
    @Headers("auth: required")
    suspend fun getCanceledSells(
        @Header("access_token") accessToken: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("page") page: Long?,
        @Query("pageSize") pageSize: Int?,
        @Query("nsu") nsu: String?,
        @Query("saleAmount") saleAmount: Double?,
        @Query("refundAmount") refundAmount: Double?,
        @Query("paymentTypes") paymentTypes: List<Int>?,
        @Query("cardBrands") cardBrands: List<Int>?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("tid") tid: String?
    ): Response<ResultSummaryCanceledSales?>


    @GET("site-cielo/v1/sales/history")
    suspend fun getSummarySalesHistory(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("type") type: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("cardBrands") cardBrand: List<Int>?,
        @Query("paymentTypes") paymentType: List<Int>?
    ): Response<ResultSummarySalesHistory?>


    @GET("site-cielo/v1/merchant")
    suspend fun getMerchant(
        @Header("authorization") authorization: String,
        @Header("access_token") accessToken: String
    ): Response<UserOwnerResponse?>


    @GET("site-cielo/v1/statement/card-brands")
    suspend fun getCardBrands(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String
    ): Response<ResultCardBrands?>


    @GET("site-cielo/v1/sales/filters")
    suspend fun getPaymentTypes(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?
    ): Response<ResultPaymentTypes?>

    @GET("/site-cielo/v1/sales/refunds/filters")
    @Headers("auth: required")
    suspend fun filterCanceledSells(
        @Header("access_token") accessToken: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?
    ): Response<ResultPaymentTypes?>
}