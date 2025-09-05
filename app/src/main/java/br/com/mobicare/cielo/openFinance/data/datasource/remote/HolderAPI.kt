package br.com.mobicare.cielo.openFinance.data.datasource.remote

import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.data.model.response.BrandResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ChangeOrRenewShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ConfirmShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentDetailResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.data.model.response.CreateShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.data.model.response.GivenUpShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.PixMerchantOpenFinanceResponse
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse
import br.com.mobicare.cielo.openFinance.data.model.response.UpdateShareResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HolderAPI {
    @GET("/site-cielo/v1/pix/merchant/list")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getPixMerchantListOpenFinance(
    ): Response<List<PixMerchantOpenFinanceResponse>?>

    @GET("/site-cielo/v1/openfinance/detainer")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getDetainer(
        @Query("consentId", encoded = true) consentId: String
    ): Response<DetainerResponse>

    @GET("site-cielo/v1/prepaid/cards/{proxy}/balance")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getUserCardBalance(
        @Path("proxy") cardProxy: String
    ): Response<PrepaidBalanceResponse>

    @PUT("site-cielo/v1/openfinance/detainer/approve")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putApproveConsent(
        @Body consentId: ConsentIdRequest,
        @Header("otpCode") otpCode: String,
    ): Response<ConsentResponse>

    @PUT("site-cielo/v1/openfinance/detainer/reject")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putRejectConsent(
        @Query("version") version: String,
        @Body rejectConsentRequest: RejectConsentRequest?
    ): Response<ConsentResponse>

    @GET("/site-cielo/v1/openfinance/receiver/listConsents")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getConsents(
        @Query("journey", encoded = true) journey: String,
        @Query("page", encoded = true) page: String,
        @Query("pageSize", encoded = true) pageSize: String?,
    ): Response<SharedDataConsentsResponse>

    @GET("/site-cielo/v1/openfinance/receiver/consent/detail")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getConsentDetail(
        @Query("consentId", encoded = true) consentId: String
    ): Response<ConsentDetailResponse>

    @GET("/site-cielo/v1/openfinance/receiver/brands")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getBrands(
        @Query("name", encoded = true) name: String
    ): Response<List<BrandResponse>>

    @POST("/site-cielo/v1/openfinance/receiver/create-share")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun createShare(
        @Body createShareRequest: CreateShareRequest
    ): Response<CreateShareResponse>

    @POST("/site-cielo/v1/openfinance/receiver/update-share")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun updateShare(
        @Query("shareId", encoded = true) shareId: String,
        @Body updateShareRequest: UpdateShareRequest
    ): Response<UpdateShareResponse>

    @GET("/site-cielo/v1/openfinance/receiver/termsOfUse")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getTermsOfUse(
    ): Response<String>

    @POST("/site-cielo/v1/openfinance/receiver/consent/confirm")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun confirmShare(
        @Body confirmShareRequest: ConfirmShareRequest
    ): Response<ConfirmShareResponse>

    @POST("/site-cielo/v1/openfinance/receiver/consent/givenup")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun givenUpShare(
        @Body updateShareRequest: GivenUpShareRequest
    ): Response<GivenUpShareResponse>

    @POST("/site-cielo/v1/openfinance/receiver/consent/update")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun changeOrRenewShare(
        @Body changeOrRenewShareRequest: ChangeOrRenewShareRequest
    ): Response<ChangeOrRenewShareResponse>

    @POST("/site-cielo/v1/openfinance/receiver/consent/terminate")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun endSharing(
        @Header("otpCode") otpCode: String,
        @Body endShareRequest: EndShareRequest
    ): Response<Any>
}