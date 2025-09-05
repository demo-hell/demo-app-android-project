package br.com.mobicare.cielo.selfieChallange.domain.usecase

import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository

class PostSelfieChallengeUseCase(
    private val repository: SelfieChallengeRepository
) {
    suspend operator fun invoke(
        base64: String?,
        encrypted: String?,
        username: String?,
        operation: String
    ) = repository.postSelfieChallenge(
        base64, encrypted, username, operation
    )
}