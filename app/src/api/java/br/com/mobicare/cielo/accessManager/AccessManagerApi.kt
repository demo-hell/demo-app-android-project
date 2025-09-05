package br.com.mobicare.cielo.accessManager

import br.com.mobicare.cielo.accessManager.addUser.model.*
import br.com.mobicare.cielo.accessManager.model.*
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingCustomerSettingsResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface AccessManagerApi {
    /** Swagger:
    https://digitalhml.hdevelo.com.br/accounts/swagger.json
     **/

    @GET("site-cielo/v1/accounts/management/access/users-no-role")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getNoRoleUsers(): Observable<List<AccessManagerUser>?>

    @GET("site-cielo/v1/accounts/management/access/users-with-role")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getUsersWithRole(): Observable<List<AccessManagerUser>?>

    @GET("site-cielo/v1/accounts/management/access/users-with-role")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getCustomUsersWithRole(
        @Query ("customProfilesOnly") customProfilesOnly: Boolean,
        @Query ("profile") profileId: String
    ): Observable<List<AccessManagerUser>?>

    @POST("site-cielo/v1/accounts/management/access/assign-role")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun assignRole(
        @Body requestList: List<AccessManagerAssignRoleRequest>, @Header("otpCode") otpCode: String
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/profile")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getProfiles(
        @Query ("profileType") profileType: String,
        @Query ("status") status : String,
        @Query ("fetchDetails") fetchDetails : Boolean
    ): Observable<List<AccessManagerCustomProfileResponse>>

    @GET("site-cielo/v1/accounts/profile/{profileId}")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getProfileDetail(
        @Path("profileId") profileId: String
    ): Observable<AccessManagerCustomProfileDetailResponse>

    /*INVITATION*/

    @POST("site-cielo/v1/accounts/management/access/create-invite")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun sendIvite(
        @Body accessManagerSendInviteRequest: AccessManagerSendInviteRequest,
        @Header("otpCode") otpCode: String
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/management/access/merchant/children")
    @Headers("auth: required")
    fun getMerchants(
        @Header("access_token") accessToken: String,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
    ): Observable<AccessManagerMerchants>

    @GET("site-cielo/v1/merchant/children")
    @Headers(value = ["accessToken: no-required"])
    fun children(
        @Header("access_token") token: String,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("searchCriteria") searchCriteria: String? = null
    ): Observable<HierachyResponse>


    @POST("site-cielo/v1/accounts/management/access/validate-invite")
    @Headers(value = ["accessToken: no-required"])
    fun validateCpf(
        @Header("access_token") token: String,
        @Body accessmanagervalidateuserrequest: AccessManagerValidateCpfRequest
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/accounts/management/access/validate-invite")
    @Headers(value = ["accessToken: no-required"])
    fun validateEmail(
        @Header("access_token") token: String,
        @Body accessmanagervalidateuserrequest: AccessManagerValidateEmailRequest
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/countries")
    @Headers(value = ["accessToken: no-required"])
    fun getCountries(
        @Header("access_token") token: String
    ): Observable<MutableList<Country>>

    @GET("site-cielo/v1/accounts/countries/ddi")
    @Headers(value = ["accessToken: no-required"])
    fun getCountriesDDI(
        @Header("access_token") token: String
    ): Observable<MutableList<Country>>

    @POST("site-cielo/v1/accounts/management/access/unlink-user")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun unlinkUser(
        @Body requestList: List<AccessManagerUnlinkUserRequest>, @Header("otpCode") otpCode: String
    ): Observable<AccessManagerUnlinkUserResponse>

    @POST("site-cielo/v1/accounts/management/access/resend-invites")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun resendInvite(
        @Header("otpCode") otpCode: String,
        @Body body: List<String>
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/management/access/invites")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getExpiredInvites(
        @Query("pageNumber") pageNumber: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("expired") expired: Boolean
    ): Observable<AccessManagerExpiredInviteResponse>

    @POST("site-cielo/v1/accounts/invite/accept/{inviteId}")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun acceptInvite(
        @Path("inviteId") inviteId: String
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/accounts/invite/deny/{inviteId}")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun declineInviteLoggedToken(@Path("inviteId") inviteId: String): Observable<Response<Void>>

    @POST("site-cielo/v1/accounts/invite/accept")
    fun acceptInviteToken(@Header("inviteToken") inviteToken: String): Observable<Response<Void>>

    @POST("site-cielo/v1/accounts/invite/deny")
    fun declineInviteToken(@Header("inviteToken") inviteToken: String): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/invite/details")
    fun getInviteDetails(@Header("inviteToken") inviteToken: String): Observable<AccessManagerInviteDetailsResponse>

    @GET("site-cielo/v1/accounts/invite/pending")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getPendingInvites(): Observable<AccessManagerPendingInvitesResponse>

    @POST("site-cielo/v1/accounts/management/access/remove-invites")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun deleteInvite(
        @Header("otpCode") otpCode: String,
        @Body body: List<String>
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/management/access/foreign-users/pending")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getPendingForeignUsers(
        @Query("pageNumber") pageNumber: Int?,
        @Query("pageSize") pageSize: Int?
    ): Observable<AccessManagerPendingForeignUsersResponse>

    @GET("site-cielo/v1/accounts/management/access/foreign-users/pending/{userId}")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getForeignUserDetail(
        @Path("userId") userId: String
    ): Observable<AccessManagerForeignUserDetailResponse>

    @POST("site-cielo/v1/accounts/management/access/foreign-users/decision")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun sendForeignUserDecision(
        @Header("otpCode") otpCode: String,
        @Body body: ForeignUserDecisionRequest
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/accounts/customer/settings")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getCustomerSettings(): Observable<IDOnboardingCustomerSettingsResponse>
}