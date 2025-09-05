package br.com.mobicare.cielo.newRecebaRapido.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote.ReceiveAutomaticRemoteDataSource
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository

class ReceiveAutomaticOffersRepositoryImpl(
    private val remoteDataSource: ReceiveAutomaticRemoteDataSource
): ReceiveAutomaticOffersRepository {
    override suspend fun getReceiveAutomaticOffers(
        periodicity: String?,
        nextValidityPeriod: Boolean?
    ): CieloDataResult<List<Offer>> =
        remoteDataSource.getReceiveAutomaticOffers(periodicity, nextValidityPeriod)

    override suspend fun contractReceiveAutomaticOffer(params: ReceiveAutomaticContractRequest): CieloDataResult<Void> =
        remoteDataSource.contractReceiveAutomaticOffer(params)

}