package br.com.mobicare.cielo.commons.data.dataSource.remote

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.remote.UserInformationServerApi
import br.com.mobicare.cielo.commons.data.mapper.MapperLoginObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse

class UserInformationRemoteDataSource(
    private val serverApi: UserInformationServerApi,
    private val safeApiCaller: SafeApiCaller,
    private val analytics: Analytics.Update,
    private val menuPreference: MenuPreference
) {
    private val errorDefault = CieloDataResult
        .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    suspend fun getUserInformation(): CieloDataResult<MeResponse> {
        var result: CieloDataResult<MeResponse> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getUserInformation()
        }.onSuccess { response ->
            response.body()?.let { body ->
                result = CieloDataResult.Success(body)
                val loginObj = MapperLoginObj.mapToLoginObj(body)
                menuPreference.saveLoginObj(loginObj)

                analytics.updateUserId()
                analytics.updateUserProperties()
            } ?: CieloDataResult.Empty(response.code())
        }.onError { error ->
            result = error
        }

        return result
    }
}