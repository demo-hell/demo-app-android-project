package br.com.mobicare.cielo.newRecebaRapido.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository

class ContractReceiveAutomaticOfferUseCase(
    private val repository: ReceiveAutomaticOffersRepository
) {
    suspend operator fun invoke(params: ReceiveAutomaticContractRequest): CieloDataResult<Void> =
         repository.contractReceiveAutomaticOffer(params)
}