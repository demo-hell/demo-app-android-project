package br.com.mobicare.cielo.forgotMyPassword.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.forgotMyPassword.data.dataSource.remote.ForgotMyPasswordServerAPI
import br.com.mobicare.cielo.forgotMyPassword.data.mapper.MapperForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword

class ForgotMyPasswordRemoteDataSource(
    private val serverAPI: ForgotMyPasswordServerAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun postRecoveryPassword(
        params: ForgotMyPasswordRecoveryPasswordRequest,
        akamaiSensorData: String?
    ): CieloDataResult<ForgotMyPassword> {
        var result: CieloDataResult<ForgotMyPassword> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverAPI.postRecoveryPassword(
                params,
                akamaiSensorData
            )
        }.onSuccess { response ->
            result = MapperForgotMyPassword.mapToForgotMyPassword(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = it
        }.onEmpty { return result }

        return result
    }

}