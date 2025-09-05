package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.mySales.data.model.bo.HomeCardSummarySaleBO
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository
import br.com.mobicare.cielo.mySales.domain.usecase.UseCaseUtils.isSalesEmpty

class GetHomeCardSummarySalesUseCase(private val repository: MySalesRemoteRepository) {

    suspend operator fun invoke(params: GetSalesDataParams): CieloDataResult<HomeCardSummarySaleBO> {
        lateinit var result: CieloDataResult<HomeCardSummarySaleBO>
        repository.getSummarySales(params)
            .onSuccess {
                it.summary.let { summary ->
                    result = if (isSalesEmpty(summary) && it.items.isEmpty()) {
                        CieloDataResult.Empty()
                    } else {
                        val homeCardSummary = HomeCardSummarySaleBO(
                            summary = summary,
                            lastSale = it.items.last()
                        )
                        CieloDataResult.Success(homeCardSummary)
                    }
                }
            }
            .onError {
                result = it
            }
            .onEmpty {
                result = it
            }
        return result
    }
}