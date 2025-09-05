package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.domain.repository.MySalesFiltersRemoteRepository
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO

class GetPaymentTypeUseCase(private val repository: MySalesFiltersRemoteRepository) {

    suspend operator fun invoke(params: GetBrandsSalesFiltersParams):
            CieloDataResult<ResultPaymentTypesBO> {
        return repository.getFilteredPaymentTypes(params)

    }
}