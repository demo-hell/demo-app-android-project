package br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote

import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.PutAdditionalInfoRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.UserUpdateDataRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.UserValidateDataRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PutAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.UserUpdateDataResponse
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.UserValidateDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface MyAccountApi {
    @POST("/site-cielo/v1/accounts/user/validate-data")
    suspend fun postUserValidateData(
        @Body body: UserValidateDataRequest
    ): Response<UserValidateDataResponse>

    @PUT("/site-cielo/v1/accounts/user/update-data")
    suspend fun putUserUpdateData(
        @Header("faceid-token") faceIdToken: String,
        @Body body: UserUpdateDataRequest
    ): Response<UserUpdateDataResponse>

    @GET("site-cielo/v1/accounts/contact-info/domain")
    suspend fun getAdditionalFieldsInfo(): Response<GetAdditionalInfoFields>

    @PUT("site-cielo/v1/accounts/contact-info")
    suspend fun putAdditionalInfo(
        @Body body: PutAdditionalInfoRequest
    ): Response<PutAdditionalInfo>

}