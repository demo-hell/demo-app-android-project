package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.mySales.data.model.bo.HomeCardSummarySaleBO
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository
import br.com.mobicare.cielo.mySales.domain.usecase.UseCaseUtils.isSalesEmpty

class GetSalesUseCase(private val repository: MySalesRemoteRepository) {
    suspend operator fun invoke(params: GetSalesDataParams): CieloDataResult<SummarySalesBO> {
        lateinit var result: CieloDataResult<SummarySalesBO>
        repository.getSummarySales(params)
            .onSuccess { result = CieloDataResult.Success(it) }
            .onError { result = it }
            .onEmpty { result = it }

        return result
    }
}