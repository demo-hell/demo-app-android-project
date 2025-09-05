package br.com.mobicare.cielo.commons.domain.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface UserPreferencesLocalRepository {
    suspend fun get(key: String, isProtected: Boolean): CieloDataResult<Set<String>>
    suspend fun put(key: String, value: String, isProtected: Boolean): CieloDataResult<Boolean>
    suspend fun put(key: String, value: Set<String>, isProtected: Boolean): CieloDataResult<Boolean>
    suspend fun delete(key: String): CieloDataResult<Boolean>
}