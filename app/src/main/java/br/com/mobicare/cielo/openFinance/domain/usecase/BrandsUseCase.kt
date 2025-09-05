package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.repository.BrandsRemoteRepository

class BrandsUseCase(private val repository: BrandsRemoteRepository) {
    suspend operator fun invoke(name: String): CieloDataResult<List<Brand>> {
        return repository.getBrands(name)
    }
}