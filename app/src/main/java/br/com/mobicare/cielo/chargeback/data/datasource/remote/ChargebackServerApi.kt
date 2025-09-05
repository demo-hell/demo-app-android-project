package br.com.mobicare.cielo.chargeback.data.datasource.remote

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackAcceptResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackDocumentResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackDocumentSenderResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackFilterResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackRefuseResponse
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebacksResponse
import br.com.mobicare.cielo.chargeback.data.model.response.LifecycleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Query

interface ChargebackServerApi {

    @GET("site-cielo/v1/sales/chargebacks/search")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getChargebackList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("status") status: String,
        @Query("orderBy") orderBy: String,
        @Query("order") order: String,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null,
        @Query("cardBrands") cardBrand: ArrayList<Int>? = null,
        @Query("processCode") processCode: ArrayList<Int>? = null,
        @Query("reasonCode") reasonCode: ArrayList<Int>? = null,
        @Query("caseId") caseId: Int? = null,
        @Query("tid") tid: String? = null,
        @Query("nsu") nsu: String? = null,
        @Query("disputeStatus") disputeStatus: ArrayList<Int>? = null
    ): Response<ChargebacksResponse>

    @PUT("site-cielo/v1/sales/chargebacks/refuse")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putChargebackRefuse(
        @Header("otpCode") otpCode: String,
        @Body body: ChargebackRefuseRequest
    ): Response<ChargebackRefuseResponse>

    @PUT("site-cielo/v1/sales/chargebacks/accept")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putChargebackAccept(
        @Header("otpCode") otpCode: String,
        @Body body: ChargebackAcceptRequest
    ): Response<ChargebackAcceptResponse>

    @GET("site-cielo/v1/sales/chargebacks/lifecycle")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getChargebackLifecycle(@Query("caseId") caseId: Int): Response<List<LifecycleResponse>>

    @GET("site-cielo/v1/sales/chargebacks/documents")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getChargebackDocument(
        @Query("merchantId") merchantId: Long,
        @Query("chargebackId") chargebackId: Int,
    ): Response<ChargebackDocumentResponse>


    @GET("site-cielo/v1/sales/chargebacks/search/options")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getChargebackFilters(): Response<ChargebackFilterResponse>

    @GET("site-cielo/v1/sales/chargebacks/proof/export")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getChargebackDocumentSender(
        @Query("merchantId") merchantId: Long,
        @Query("documentId") documentId: Int,
    ): Response<ChargebackDocumentSenderResponse>

}