package br.com.mobicare.cielo.commons.utils.token.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.utils.token.domain.repository.TokenRepository

class GetTokenUseCase(private val repository: TokenRepository) {
    operator fun invoke(): CieloDataResult<String> = repository.getToken()
}