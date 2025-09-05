package br.com.mobicare.cielo.pix.api

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.model.PixBank
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface PixAPI {

    @GET("/site-cielo/v1/pix/keys/all")
    fun getKeys(
            @Header("Authorization") bearerToken: String?
    ): Observable<PixKeysResponse>

    @POST("/site-cielo/v1/pix/keys")
    fun createKey(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: CreateKeyRequest
    ): Observable<CreateKeyResponse>

    @HTTP(method = "DELETE", path = "/site-cielo/v1/pix/keys", hasBody = true)
    fun deleteKey(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixKeyDeleteRequest
    ): Observable<PixKeyDeleteRequest>

    @GET("/site-cielo/v1/pix/keys/validate")
    fun validateKey(
            @Header("Authorization") bearerToken: String?,
            @Query("key") key: String?,
            @Query("keyType") keyType: String?
    ): Observable<ValidateKeyResponse>

    @GET("/site-cielo/v1/pix/transfer/details")
    fun getTransferDetails(
            @Header("Authorization") bearerToken: String?,
            @Query("endToEndId") endToEndId: String?,
            @Query("transactionCode") transactionCode: String?
    ): Observable<TransferDetailsResponse>

    @POST("/site-cielo/v1/pix/transfer/key")
    fun transfer(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: TransferRequest?
    ): Observable<PixTransferResponse>

    @POST("/site-cielo/v1/pix/transfer/accountbank")
    fun transferToBankAccount(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixManualTransferRequest?
    ): Observable<PixTransferResponse>

    @GET("/site-cielo/v1/pix/transfer/banks")
    fun getAllBanks(
            @Header("Authorization") bearerToken: String?
    ): Observable<List<PixBank>>

    @POST("/site-cielo/v1/pix/claims")
    fun createClaims(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: ClaimsRequest?
    ): Observable<ClaimsResponse>

    @POST("/site-cielo/v1/pix/claims/revoke")
    fun revokeClaims(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: RevokeClaimsRequest?
    ): Observable<RevokeClaimsResponse>

    @POST("/site-cielo/v1/pix/claims/confirm")
    fun confirmClaims(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: ConfirmClaimsRequest?
    ): Observable<ConfirmClaimsResponse>

    @POST("/site-cielo/v1/pix/keys/verify")
    fun requestValidateCode(
            @Header("Authorization") bearerToken: String?,
            @Body body: ValidateCode?
    ): Observable<ValidateCode>

    @POST("/site-cielo/v1/pix/qrcode/charge")
    fun chargeQRCode(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: QRCodeChargeRequest?
    ): Observable<QRCodeChargeResponse>

    @POST("/site-cielo/v1/pix/qrcode/decode")
    fun decodeQRCode(
            @Header("Authorization") bearerToken: String?,
            @Body body: QRCodeDecodeRequest?
    ): Observable<QRCodeDecodeResponse>

    @GET("/site-cielo/v1/pix/receipts")
    fun getExtract(
        @Header("Authorization") authorization: String?,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("limit") limit: Int?,
        @Query("idEndToEnd") idEndToEnd: String?,
        @Query("receiptsTab") receiptsTab: String,
        @Query("schedulingCode") schedulingCode: String?,
        @Query("period") period: String?,
        @Query("transferType") transferType: String?,
        @Query("cashFlowType") cashFlowType: String?,
    ): Observable<PixExtractResponse>

    @HTTP(method = "DELETE", path = "/site-cielo/v1/pix/transfer/schedule", hasBody = true)
    @Headers(value = ["auth: required", "accessToken: required"])
    fun cancelTransactionScheduled(
            @Header("Authorization") authorization: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: ScheduleCancelRequest,
    ): Observable<ScheduleCancelResponse>

    @GET("site-cielo/v1/prepaid/cards/{proxy}/balance")
    fun fetchUserCardBalance(
            @Path("proxy") cardProxy: String,
            @Header("access_token") accessToken: String
    ): Observable<PrepaidBalanceResponse>

    @GET("/site-cielo/v1/pix/merchant")
    fun getMerchant(
            @Header("Authorization") bearerToken: String?,
    ): Observable<PixMerchantResponse>

    @GET("/site-cielo/v1/pix/profile")
    fun getProfile(
            @Header("Authorization") bearerToken: String?,
    ): Observable<PixProfileResponse>

    @PUT("/site-cielo/v1/pix/profile")
    fun updateProfile(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixProfileRequest?
    ): Observable<Response<Void>>

    @GET("/site-cielo/v1/pix/limits/trusted-destinations")
    fun getTrustedDestinations(
            @Header("Authorization") bearerToken: String?,
            @Query("serviceGroup") servicesGroup: String?
    ): Observable<List<PixTrustedDestinationResponse>>

    @POST("/site-cielo/v1/pix/limits/trusted-destinations")
    fun addNewTrustedDestination(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixAddNewTrustedDestinationRequest?
    ): Observable<PixAddNewTrustedDestinationResponse>

    @HTTP(
            method = "DELETE",
            path = "/site-cielo/v1/pix/limits/trusted-destinations",
            hasBody = true
    )
    @Headers(value = ["auth: required", "accessToken: required"])
    fun deleteTrustedDestination(
            @Header("Authorization") authorization: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixDeleteTrustedDestinationRequest,
    ): Observable<Response<Void>>

    @GET("/site-cielo/v1/pix/limits")
    fun getLimits(
            @Header("Authorization") bearerToken: String?,
            @Query("beneficiaryType") beneficiaryType: String?,
            @Query("serviceGroup") serviceGroup: String?,
    ): Observable<PixMyLimitsResponse>

    @PUT("/site-cielo/v1/pix/limits")
    fun updateLimit(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixMyLimitsRequest?
    ): Observable<Response<Void>>

    @GET("/site-cielo/v1/pix/limits/nighttime")
    fun getNightTime(
            @Header("Authorization") bearerToken: String?,
    ): Observable<PixTimeManagementResponse>

    @PUT("/site-cielo/v1/pix/limits/nighttime")
    fun updateNightTime(
            @Header("Authorization") bearerToken: String?,
            @Header("otpCode") otpCode: String?,
            @Body body: PixTimeManagementRequest?
    ): Observable<Response<Void>>

    @GET("/site-cielo/v1/pix/refunds/receipts")
    fun getReceipts(
        @Header("Authorization") bearerToken: String?,
        @Query("idEndToEndOriginal") idEndToEndOriginal: String?,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("date") date: String? = null,
        @Query("limit") limit: Int? = TWENTY_FIVE,
    ): Observable<ReversalReceiptsResponse>

    @POST("/site-cielo/v1/pix/refunds")
    fun reverse(
        @Header("Authorization") bearerToken: String?,
        @Header("otpCode") otpCode: String?,
        @Body body: ReversalRequest?
    ): Observable<PixReversalResponse>

    @GET("/site-cielo/v1/pix/refunds/detail")
    fun getReversalDetails(
        @Header("Authorization") bearerToken: String?,
        @Query("transactionCode") transactionCode: String? = null
        ): Observable<ReversalDetailsResponse>

    @GET("/site-cielo/v1/pix/refunds/detail/full")
    fun getReversalDetailsFull(
        @Header("Authorization") bearerToken: String?,
        @Query("transactionCode") transactionCode: String? = null
        ): Observable<ReversalDetailsFullResponse>

    @GET("/site-cielo/v1/pix/transfer/schedule/detail")
    fun getScheduleDetail(
        @Header("Authorization") bearerToken: String?,
        @Header("schedulingCode") schedulingCode: String?
    ): Observable<SchedulingDetailResponse>
}