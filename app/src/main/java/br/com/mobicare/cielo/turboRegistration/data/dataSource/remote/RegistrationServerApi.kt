package br.com.mobicare.cielo.turboRegistration.data.dataSource.remote

import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.BankResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.EligibilityResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.MccResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.OperationsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RegistrationServerApi {

    @GET("site-cielo/v1/merchant/registration/eligibility")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getEligibility(): Response<EligibilityResponse>

    @GET("site-cielo/v1/merchant/payment-accounts/operation-bank")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAllOperations(): Response<OperationsResponse>

    @GET("site-cielo/v1/merchant/addresses/cep/{cep}")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAddressByCep(@Path("cep") cep: String): Response<AddressResponse>

    @GET("site-cielo/v1/accounts/banks")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAllBanks(): Response<BankResponse>

    @GET("site-cielo/v1/merchant/registration/mcc")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getAllBusinessSector(): Response<MccResponse>

    @PUT("site-cielo/v1/merchant/registration/addresses/{id}")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun updateAddress(
        @Path("id") idAddress: String,
        @Body address: AddressRequest
    ): Response<Void>

    @PUT("site-cielo/v1/merchant/registration/mcc")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun updateBusinessSector(@Body body: BusinessUpdateRequest): Response<Void>

    @POST("site-cielo/v1/merchant/registration/billing")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun updateMonthlyIncome(@Body billingRequest: BillingRequest): Response<Void>

    @POST("site-cielo/v1/merchant/registration/payment-accounts")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun registerNewAccount(@Body paymentAccountRequest: PaymentAccountRequest): Response<Void>

}