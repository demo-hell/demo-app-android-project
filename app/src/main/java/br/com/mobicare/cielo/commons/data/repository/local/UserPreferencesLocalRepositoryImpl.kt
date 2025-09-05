package br.com.mobicare.cielo.commons.data.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.dataSource.local.UserPreferencesLocalDataSource
import br.com.mobicare.cielo.commons.domain.repository.local.UserPreferencesLocalRepository

class UserPreferencesLocalRepositoryImpl(private val local: UserPreferencesLocalDataSource): UserPreferencesLocalRepository {
    override suspend fun get(key: String, isProtected: Boolean): CieloDataResult<Set<String>> {
        return local.get(key, isProtected)
    }

    override suspend fun put(key: String, value: String, isProtected: Boolean): CieloDataResult<Boolean> {
        return local.put(key, value, isProtected)
    }

    override suspend fun put(key: String, value: Set<String>, isProtected: Boolean): CieloDataResult<Boolean> {
        return local.put(key, value, isProtected)
    }

    override suspend fun delete(key: String): CieloDataResult<Boolean> {
        return local.delete(key)
    }
}