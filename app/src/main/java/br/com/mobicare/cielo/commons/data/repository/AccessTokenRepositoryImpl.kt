package br.com.mobicare.cielo.commons.data.repository

import br.com.mobicare.cielo.commons.data.dataSource.AccessTokenDataSource
import br.com.mobicare.cielo.commons.domain.repository.AccessTokenRepository

class AccessTokenRepositoryImpl(private val dataSource: AccessTokenDataSource) :
    AccessTokenRepository {
    override suspend fun getAccessToken() = dataSource.getAccessToken()
}