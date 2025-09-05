package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository

class GetSalesHistoryUseCase(private val repository: MySalesRemoteRepository) {
    suspend operator fun invoke(params: GetSalesHistoryParams): CieloDataResult<ResultSummarySalesHistoryBO> {
        lateinit var result: CieloDataResult<ResultSummarySalesHistoryBO>

        repository.getSummarySalesHistory(params)
            .onSuccess {
                result = if (it.items.isNullOrEmpty().not()) {
                    CieloDataResult.Success(it)
                } else {
                    CieloDataResult.Empty()
                }
            }
            .onError { result = it }
            .onEmpty { result = it }

        return result
    }
}