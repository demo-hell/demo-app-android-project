package br.com.mobicare.cielo.pixMVVM.data.datasource.remote

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.OnBoardingFulfillmentResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAccountBalanceResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAuthorizationStatusResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixDecodeQRCodeResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixExtractResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixReceiptsScheduledResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundCreatedResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailFullResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundReceiptsResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixSchedulingDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferBankResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferResultResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixValidateKeyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface PixServiceApi {
    @GET("site-cielo/v1/pix/onboarding/fulfillment")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getOnBoardingFulfillment(): Response<OnBoardingFulfillmentResponse>

    @GET("site-cielo/v1/pix/digital-account/balance")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getPixAccountBalance(): Response<PixAccountBalanceResponse>

    @GET("site-cielo/v1/pix/keys/all")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAllKeys(): Response<PixKeysResponse>

    @GET("site-cielo/v1/merchant/solutions/pix")
    @Headers(value = ["auth: required"])
    suspend fun getPixAuthorizationStatus(): Response<PixAuthorizationStatusResponse>

    @GET("/site-cielo/v1/pix/receipts")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getExtract(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("idEndToEnd") idEndToEnd: String? = null,
        @Query("receiptsTab") receiptsTab: String? = null,
        @Query("schedulingCode") schedulingCode: String? = null,
        @Query("schedulingStatus") schedulingStatus: String? = null,
        @Query("period") period: String? = null,
        @Query("transferType") transferType: String? = null,
        @Query("cashFlowType") cashFlowType: String? = null,
    ): Response<PixExtractResponse>

    @GET("/site-cielo/v1/pix/receipts/scheduled")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getReceiptsScheduled(
        @Query("limit") limit: Int? = null,
        @Query("schedulingStartDate") schedulingStartDate: String? = null,
        @Query("schedulingEndDate") schedulingEndDate: String? = null,
        @Query("lastSchedulingIdentifierCode") lastSchedulingIdentifierCode: String? = null,
        @Query("lastNextDateTimeScheduled") lastNextDateTimeScheduled: String? = null,
    ): Response<PixReceiptsScheduledResponse>

    @GET("/site-cielo/v1/pix/refunds/receipts")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getRefundReceipts(
        @Query("idEndToEndOriginal") idEndToEndOriginal: String?,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("date") date: String? = null,
        @Query("limit") limit: Int? = TWENTY_FIVE,
    ): Response<PixRefundReceiptsResponse>

    @POST("/site-cielo/v1/pix/refunds")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun refund(
        @Header("otpCode") otpCode: String?,
        @Body body: PixRefundCreateRequest?,
    ): Response<PixRefundCreatedResponse>

    @GET("/site-cielo/v1/pix/refunds/detail")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getRefundDetail(
        @Query("transactionCode") transactionCode: String? = null,
    ): Response<PixRefundDetailResponse>

    @GET("/site-cielo/v1/pix/refunds/detail/full")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getRefundDetailFull(
        @Query("transactionCode") transactionCode: String? = null,
    ): Response<PixRefundDetailFullResponse>

    @GET("/site-cielo/v1/pix/transfer/details")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getTransferDetails(
        @Query("endToEndId") endToEndId: String?,
        @Query("transactionCode") transactionCode: String?,
    ): Response<PixTransferDetailResponse>

    @POST("/site-cielo/v1/pix/transfer/key")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun transferWithKey(
        @Header("otpCode") otpCode: String?,
        @Body body: PixTransferKeyRequest?,
    ): Response<PixTransferResultResponse>

    @POST("/site-cielo/v1/pix/transfer/accountbank")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun transferToBankAccount(
        @Header("otpCode") otpCode: String?,
        @Body body: PixTransferBankAccountRequest?,
    ): Response<PixTransferResultResponse>

    @GET("/site-cielo/v1/pix/transfer/banks")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getTransferBanks(): Response<List<PixTransferBankResponse>>

    @HTTP(method = "DELETE", path = "/site-cielo/v1/pix/transfer/schedule", hasBody = true)
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun cancelTransferSchedule(
        @Header("otpCode") otpCode: String?,
        @Body body: PixScheduleCancelRequest,
    ): Response<PixTransferResultResponse>

    @GET("/site-cielo/v1/pix/transfer/schedule/detail")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getTransferScheduleDetail(
        @Header("schedulingCode") schedulingCode: String?,
    ): Response<PixSchedulingDetailResponse>

    @POST("/site-cielo/v1/pix/settlement/balance/transfer")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun transferScheduledBalance(
        @Header("otpCode") otpCode: String?,
    ): Response<Unit>

    @GET("site-cielo/v1/pix/infringement")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getInfringement(
        @Query("idEndToEnd") idEndToEnd: String,
    ): Response<PixEligibilityInfringementResponse>

    @POST("site-cielo/v1/pix/infringement")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postInfringement(
        @Body body: PixCreateNotifyInfringementRequest,
    ): Response<PixCreateNotifyInfringementResponse>

    @GET("/site-cielo/v1/pix/keys/validate")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getValidateKey(
        @Query("key") key: String?,
        @Query("keyType") keyType: String?,
    ): Response<PixValidateKeyResponse>

    @PUT("/site-cielo/v1/pix/profile")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putProfile(
        @Header("otpCode") otpCode: String?,
        @Body body: PixProfileRequest,
    ): Response<String>

    @POST("/site-cielo/v1/pix/settlement/scheduled")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postScheduledSettlement(
        @Header("otpCode") otpCode: String?,
        @Body body: PixScheduledSettlementRequest,
    ): Response<PixScheduledSettlementResponse>

    @PUT("/site-cielo/v1/pix/settlement/scheduled")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putScheduledSettlement(
        @Header("otpCode") otpCode: String?,
        @Body body: PixScheduledSettlementRequest,
    ): Response<PixScheduledSettlementResponse>

    @POST("/site-cielo/v1/pix/qrcode/decode")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postDecodeQRCode(
        @Body body: PixDecodeQRCodeRequest,
    ): Response<PixDecodeQRCodeResponse>
}
