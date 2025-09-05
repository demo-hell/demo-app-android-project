package br.com.mobicare.cielo.newRecebaRapido.data.repository

import br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote.ReceiveAutomaticRemoteDataSource
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticEligibilityRepository

class ReceiveAutomaticEligibilityRepositoryImpl(
    private val remoteDataSource: ReceiveAutomaticRemoteDataSource,
) : ReceiveAutomaticEligibilityRepository {
    override suspend fun getReceiveAutomaticEligibility() = remoteDataSource.getReceiveAutomaticEligibility()
}
