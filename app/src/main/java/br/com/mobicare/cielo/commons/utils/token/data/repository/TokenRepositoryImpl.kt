package br.com.mobicare.cielo.commons.utils.token.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.utils.token.data.dataSource.TokenDataSource
import br.com.mobicare.cielo.commons.utils.token.domain.repository.TokenRepository

class TokenRepositoryImpl(private val dataSource: TokenDataSource) : TokenRepository {
    override fun getToken(): CieloDataResult<String> = dataSource.getToken()
}