package br.com.mobicare.cielo.commons.domain.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ConfigurationLocalRepository {
    suspend fun getConfiguration(key: String, default: String): CieloDataResult<String>
}