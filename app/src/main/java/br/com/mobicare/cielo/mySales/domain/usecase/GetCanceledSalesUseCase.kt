package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository
import br.com.mobicare.cielo.mySales.domain.usecase.UseCaseUtils.isSalesEmpty

class GetCanceledSalesUseCase(private val repository: MySalesRemoteRepository) {

    suspend operator fun invoke(params: GetCanceledSalesParams): CieloDataResult<CanceledSummarySalesBO>{

        return when(val response = repository.getCanceledSales(params)) {
            is CieloDataResult.APIError -> {
                val error = response.apiException
                CieloDataResult.APIError(error)
            }

            is CieloDataResult.Empty -> {
                CieloDataResult.Empty()
            }

            is CieloDataResult.Success<CanceledSummarySalesBO> -> {
                val responseValue = response.successValueOrNull
                var result: CieloDataResult<CanceledSummarySalesBO> = CieloDataResult.Empty()

                responseValue?.let { responseValue ->
                    if (isSalesEmpty(responseValue.summary).not()) {
                        val canceledSummarySalesBO = CanceledSummarySalesBO(
                            summary = responseValue.summary,
                            items = responseValue.items,
                            pagination = responseValue.pagination
                        )
                        result = CieloDataResult.Success(canceledSummarySalesBO)
                    }
                }
                result
            }
        }
    }
}