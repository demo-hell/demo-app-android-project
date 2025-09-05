package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.repository.AccessTokenRepository

class GetAccessTokenUseCase(private val repository: AccessTokenRepository) {
    suspend operator fun invoke(
    ): CieloDataResult<String> = repository.getAccessToken()
}