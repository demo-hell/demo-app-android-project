package br.com.mobicare.cielo.commons.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface AccessTokenRepository {
    suspend fun getAccessToken(): CieloDataResult<String>
}