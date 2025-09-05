package br.com.mobicare.cielo.selfieChallange.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.selfieChallange.data.datasource.remote.SelfieChallengeRemoteDataSource
import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository

class SelfieChallengeRepositoryImpl(
    private val remoteDataSource: SelfieChallengeRemoteDataSource,
) : SelfieChallengeRepository {
    override suspend fun getStoneAgeToken(): CieloDataResult<String> {
        return remoteDataSource.getStoneAgeToken()
    }

    override suspend fun postSelfieChallenge(
        base64: String?,
        encrypted: String?,
        username: String?,
        operation: String
    ): CieloDataResult<String> {
        return remoteDataSource.postSelfieChallenge(
            base64, encrypted, username, operation
        )
    }

}