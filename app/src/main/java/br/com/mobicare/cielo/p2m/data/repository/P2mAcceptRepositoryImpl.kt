package br.com.mobicare.cielo.p2m.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.p2m.data.datasource.remote.P2mAcceptRemoteDataSource
import br.com.mobicare.cielo.p2m.domain.repository.P2mAcceptRepository

class P2mAcceptRepositoryImpl(
    private val dataSource: P2mAcceptRemoteDataSource
) : P2mAcceptRepository {

    override suspend fun putP2mAccept(
        bannerId: String
    ): CieloDataResult<Void> =
        dataSource.putP2mAccept(bannerId)
}