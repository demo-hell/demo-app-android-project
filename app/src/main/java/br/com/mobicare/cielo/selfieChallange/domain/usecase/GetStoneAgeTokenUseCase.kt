package br.com.mobicare.cielo.selfieChallange.domain.usecase

import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository

class GetStoneAgeTokenUseCase(
    private val repository: SelfieChallengeRepository
) {
    suspend operator fun invoke() = repository.getStoneAgeToken()
}