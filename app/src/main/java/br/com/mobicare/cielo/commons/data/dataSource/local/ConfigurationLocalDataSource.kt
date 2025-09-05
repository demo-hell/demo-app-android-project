package br.com.mobicare.cielo.commons.data.dataSource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference

class ConfigurationLocalDataSource(private val configurationPreference: ConfigurationPreference) {

    fun getConfigurationValuePreference(key: String, default: String): CieloDataResult<String> {
        return try {
            val value = configurationPreference.getConfigurationValue(key, default)
            CieloDataResult.Success(value)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }
}
