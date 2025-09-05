package br.com.mobicare.cielo.commons.data.dataSource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.remote.FeatureToggleServerApi
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleParams
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse

class FeatureTogglePreferenceRemoteDataSource(
    private val serverApi: FeatureToggleServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    private val errorDefault =
        CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    suspend fun getFeatureTogglePreference(): CieloDataResult<FeatureToggleResponse> {
        var result: CieloDataResult<FeatureToggleResponse> = errorDefault

        val ftParams = FeatureToggleParams.getParams()

        safeApiCaller.safeApiCall {
            serverApi.getFeatureToggle(
                system = ftParams.system, version = ftParams.version, platform = ftParams.platform
            )
        }.onSuccess { response ->
            result = response.body()?.let { body ->
                CieloDataResult.Success(body)
            } ?: CieloDataResult.Empty()
        }.onEmpty {
            result = CieloDataResult.Empty()
        }.onError { error ->
            result = error
        }

        return result
    }
}