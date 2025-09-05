package br.com.mobicare.cielo.newRecebaRapido.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository

class GetReceiveAutomaticOffersUseCase(
    private val repository: ReceiveAutomaticOffersRepository
) {
    suspend operator fun invoke(periodicity: String? = null, nextValidityPeriod: Boolean? = null): CieloDataResult<List<Offer>> =
         repository.getReceiveAutomaticOffers(periodicity, nextValidityPeriod)
}