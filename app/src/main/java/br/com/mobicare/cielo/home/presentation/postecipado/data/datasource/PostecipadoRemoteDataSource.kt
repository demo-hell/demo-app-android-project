package br.com.mobicare.cielo.home.presentation.postecipado.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.remote.PostecipadoServerApi
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse

class PostecipadoRemoteDataSource(
    private val serverApi: PostecipadoServerApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getPlanInformation(planName: String): CieloDataResult<PlanInformationResponse> {
        var result: CieloDataResult<PlanInformationResponse> = CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverApi.getPlanInformation(planName)
        }.onSuccess {
            result = it.body()?.let { plan ->
                CieloDataResult.Success(plan)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            return result
        }

        return result
    }
}