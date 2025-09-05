package br.com.mobicare.cielo.commons.data.dataSource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics

class UserPreferencesLocalDataSource(private val userPreferences: UserPreferences) {

    fun get(key: String, isProtected: Boolean = true): CieloDataResult<Set<String>> {
        return try {
            userPreferences.get(key, setOf(), isProtected).let {
                if (it.isEmpty()) {
                    CieloDataResult.Empty()
                } else {
                    CieloDataResult.Success(it)
                }
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }

    fun put(key: String, value: String, isProtected: Boolean = true): CieloDataResult<Boolean> {
        return try {
            userPreferences.put(key, value, isProtected).let {
                CieloDataResult.Success(value = true)
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Success(value = false)
        }
    }

    fun put(key: String, value: Set<String>, isProtected: Boolean = true): CieloDataResult<Boolean> {
        return try {
            userPreferences.put(key, value, isProtected).let {
                CieloDataResult.Success(value = true)
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Success(value = false)
        }
    }

    fun delete(key: String): CieloDataResult<Boolean> {
        return try {
            userPreferences.delete(key).let {
                CieloDataResult.Success(value = true)
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Success(value = false)
        }
    }
}