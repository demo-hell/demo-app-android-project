package br.com.mobicare.cielo.selfieChallange.data.datasource.remote

import br.com.mobicare.cielo.commons.constants.Intent
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.selfieChallange.data.model.request.SelfieChallengeRequest

class SelfieChallengeRemoteDataSource(
    private val api: SelfieChallengeApi, private val safeApiCaller: SafeApiCaller
) {
    suspend fun getStoneAgeToken(): CieloDataResult<String> {
        var result: CieloDataResult<String> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            api.getStoneAgeToken()
        }.onSuccess {
            result = it.body()?.let { response ->
                response.token?.let { token ->
                    CieloDataResult.Success(
                        token
                    )
                } ?: result
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            return result
        }

        return result
    }

    suspend fun postSelfieChallenge(
        base64: String?, encrypted: String?, username: String?, operation: String
    ): CieloDataResult<String> {
        var result: CieloDataResult<String> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val request = SelfieChallengeRequest(
            operation = operation,
            imageFileType = Intent.FILE_TYPE_JPG.uppercase(),
            photoFileContentBase64 = base64,
            jwtFileContent = encrypted
        )

        val apiCall = if (username.isNullOrEmpty()) {
            api.postSelfieChallenge(operation = operation, body = request)
        } else {
            api.postSelfieChallenge(userName = username, operation = operation, body = request)
        }

        safeApiCaller.safeApiCall {
            apiCall
        }.onSuccess {
            result = it.body()?.let { response ->
                response.token?.let { token ->
                    CieloDataResult.Success(
                        token
                    )
                } ?: result
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            return result
        }

        return result
    }
}