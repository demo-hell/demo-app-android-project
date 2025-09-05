package br.com.mobicare.cielo.commons.utils.token.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface TokenRepository {
    fun getToken(): CieloDataResult<String>
}