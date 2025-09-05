package br.com.mobicare.cielo.superlink.data.repository

import br.com.mobicare.cielo.superlink.domain.datasource.SuperLinkDataSource
import br.com.mobicare.cielo.superlink.domain.repository.SuperLinkRepository

class SuperLinkRepositoryImpl(
    private val remoteDataSource: SuperLinkDataSource
) : SuperLinkRepository {

    override suspend fun isPaymentLinkActive() = remoteDataSource.isPaymentLinkActive()

}