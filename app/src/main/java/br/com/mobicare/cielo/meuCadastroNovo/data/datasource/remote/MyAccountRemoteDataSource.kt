package br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.PutAdditionalInfoRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.UserUpdateDataRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.request.UserValidateDataRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields

class MyAccountRemoteDataSource(
    private val api: MyAccountApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun postUserDataValidation(
        email: String?, password: String?, passwordConfirmation: String?, cellphone: String?
    ): CieloDataResult<String> {
        var result: CieloDataResult<String> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val request = UserValidateDataRequest(
            email,
            password,
            passwordConfirmation,
            cellphone
        )

        safeApiCaller.safeApiCall {
            api.postUserValidateData(request)
        }.onSuccess {
            result = it.body()?.let { response ->
                response.message?.let { message ->
                    CieloDataResult.Success(
                        message
                    )
                } ?: result
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }

        return result
    }

    suspend fun putUserUpdateData(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?,
        faceIdToken: String
    ): CieloDataResult<String> {
        var result: CieloDataResult<String> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val request = UserUpdateDataRequest(
            email,
            password,
            passwordConfirmation,
            cellphone
        )

        safeApiCaller.safeApiCall {
            api.putUserUpdateData(faceIdToken, request)
        }.onSuccess {
            result = it.body()?.let { response ->
                response.message?.let { message ->
                    CieloDataResult.Success(
                        message
                    )
                } ?: result
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }

        return result
    }

    suspend fun getAdditionalFieldsInfo(): CieloDataResult<GetAdditionalInfoFields> {
        var result: CieloDataResult<GetAdditionalInfoFields> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            api.getAdditionalFieldsInfo()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(response)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }

        return result
    }

    suspend fun putAdditionalInfo(
        timeOfDay: String?,
        typeOfCommunication: ArrayList<String>,
        contactPreference: String?,
        pcdType: String?
    ): CieloDataResult<String> {
        var result: CieloDataResult<String> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val request = PutAdditionalInfoRequest(
            timeOfDay,
            typeOfCommunication,
            contactPreference,
            pcdType
        )

        safeApiCaller.safeApiCall {
            api.putAdditionalInfo(request)
        }.onSuccess {
            result = it.body()?.let { response ->
                response.message?.let { message ->
                    CieloDataResult.Success(
                        message
                    )
                } ?: result
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }

        return result
    }

}