package br.com.mobicare.cielo.suporteTecnico.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.suporteTecnico.data.EquipmentEligibilityResponse
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.remote.NewTechnicalSupportAPI

class ProblemEquipmentsDataSource(
    private val serverApi: NewTechnicalSupportAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getProblemEquipments(): CieloDataResult<List<ProblemEquipments>> {

        var result: CieloDataResult<List<ProblemEquipments>> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getProblemEquipments()
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { problemEquipments ->
                CieloDataResult.Success(problemEquipments)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getEligibility(
        technology: String,
        code: String
    ): CieloDataResult<EquipmentEligibilityResponse> {

        var result: CieloDataResult<EquipmentEligibilityResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getEligibility(technology, code)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { problemEquipments ->
                CieloDataResult.Success(problemEquipments)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }
}