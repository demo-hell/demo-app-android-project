package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackFiltersRepository

class GetChargebackFiltersUseCase(private val repository: ChargebackFiltersRepository) {

    suspend operator fun invoke() = repository.getChargebackFilters()
}