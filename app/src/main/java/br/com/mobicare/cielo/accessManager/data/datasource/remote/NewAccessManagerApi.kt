package br.com.mobicare.cielo.accessManager.data.datasource.remote

import br.com.mobicare.cielo.accessManager.data.model.response.GetCustomActiveProfilesResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerAssignRoleRequest
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface NewAccessManagerApi {

    @GET("site-cielo/v1/accounts/profile")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getCustomActiveProfiles(
        @Query ("profileType") profileType: String,
        @Query ("status") status : String,
        @Query ("fetchDetails") fetchDetails : Boolean
    ): Response<List<GetCustomActiveProfilesResponse>>

    @POST("site-cielo/v1/accounts/management/access/assign-role")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postAssignRole(
        @Body requestList: List<AccessManagerAssignRoleRequest>, @Header("otpCode") otpCode: String
    ): Response<Void>
}