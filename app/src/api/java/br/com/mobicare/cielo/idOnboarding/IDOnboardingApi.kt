package br.com.mobicare.cielo.idOnboarding

import br.com.mobicare.cielo.idOnboarding.model.*
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import io.reactivex.Observable
import retrofit2.http.*

interface IDOnboardingApi {

    /** Swagger:
    https://digitalhml.hdevelo.com.br/accounts/swagger.json
     **/

    @GET("site-cielo/v1/accounts/customer/settings")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getCustomerSettings(): Observable<IDOnboardingCustomerSettingsResponse>

    @GET("site-cielo/v1/accounts/onboarding/status")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getIdOnboardingStatus(): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/start")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun setIdOnboardingStarted(): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/validate/cpf")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun validateCpfName(@Body cpfNameRequest: IDOnboardingCpfNameRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/validate/email/request-code")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun requestEmailCode(@Body emailCodeRequest: IDOnboardingSendEmailCodeRequest): Observable<IDOnboardingSendEmailCodeResponse>

    @POST("site-cielo/v1/accounts/onboarding/validate/email/check-code")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun checkEmailCode(@Body code: IDOnboardingCheckValidationCodeRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/validate/cellphone/request-code")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun requestPhoneCode(@Body phoneCodeRequest: IDOnboardingSendPhoneCodeRequest): Observable<IDOnboardingSendPhoneCodeResponse>

    @POST("site-cielo/v1/accounts/onboarding/validate/cellphone/check-code")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun checkPhoneCode(@Body code: IDOnboardingCheckValidationCodeRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/policy/p1/execute")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun validateP1Policy(): Observable<IDOnboardingStatusResponse>

    /* P2 */

    @POST("site-cielo/v1/accounts/onboarding/photo-upload/document")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun uploadDocument(@Body uploadDocumentRequest: IDOnboardingUploadDocumentRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/photo-upload/selfie")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun uploadSelfie(@Body uploadSelfieRequest: IDOnboardingUploadSelfieRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/fingerprint/analyse")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun sendAllowme(@Body sendFingerprintRequest: IDOnboardingSendFingerprintRequest): Observable<IDOnboardingStatusResponse>

    @POST("site-cielo/v1/accounts/onboarding/policy/p2/execute")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun validateP2Policy(): Observable<IDOnboardingStatusResponse>

    /* WHITELIST */

    @POST("site-cielo/v1/accounts/onboarding/whitelist/me")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun addWhitelist(): Observable<IDOnboardingWhitelistResponse>

    /* REFRESH TOKEN */

    @POST("appcielo/v1/user-login/token")
    @Headers("auth: required")
    fun refreshToken(
        @Header("access_token") accessToken: String?,
        @Header("refresh_token") refreshToken: String?
    ): Observable<LoginResponse>

    @GET("site-cielo/v1/authorities/document-scan/signed-token")
    fun getStoneAgeToken(): Observable<TokenStoneAgeResponse>

    @POST("/site-cielo/v1/accounts/onboarding/validate/cellphone/save")
    fun sendForeignCellphone(
        @Body idOnboardingSendForeignCellphoneRequest: IDOnboardingSendForeignCellphoneRequest
    ): Observable<IDOnboardingStatusResponse>
}