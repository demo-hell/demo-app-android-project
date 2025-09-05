package br.com.mobicare.cielo.posVirtual.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.data.dataSource.remote.PosVirtualAPI
import br.com.mobicare.cielo.posVirtual.data.mapper.toEntity
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual

class PosVirtualEligibilityDataSource(
    private val serverApi: PosVirtualAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getEligibility(): CieloDataResult<PosVirtual> {
        var result: CieloDataResult<PosVirtual> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall { serverApi.getEligibility() }.run {
            onSuccess { response ->
                result = response.body()?.let {
                    CieloDataResult.Success(it.toEntity())
                } ?: CieloDataResult.Empty()
            }.onError {
                result = CieloDataResult.APIError(it.apiException)
            }.onEmpty {
                result = CieloDataResult.Empty()
            }
        }

        return result
    }

}