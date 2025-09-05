package br.com.mobicare.cielo.selfieChallange.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface SelfieChallengeRepository {
    suspend fun getStoneAgeToken(): CieloDataResult<String>

    suspend fun postSelfieChallenge(
        base64: String?,
        encrypted: String?,
        username: String?,
        operation: String
    ): CieloDataResult<String>
}