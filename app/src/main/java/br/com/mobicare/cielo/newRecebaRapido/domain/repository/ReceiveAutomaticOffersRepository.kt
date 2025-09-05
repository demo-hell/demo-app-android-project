package br.com.mobicare.cielo.newRecebaRapido.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer

interface ReceiveAutomaticOffersRepository {
    suspend fun getReceiveAutomaticOffers(periodicity: String? = null, nextValidityPeriod: Boolean? = null): CieloDataResult<List<Offer>>
    suspend fun contractReceiveAutomaticOffer(params: ReceiveAutomaticContractRequest): CieloDataResult<Void>
}