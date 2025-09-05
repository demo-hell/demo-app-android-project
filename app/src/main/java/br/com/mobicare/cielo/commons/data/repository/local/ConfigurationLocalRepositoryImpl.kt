package br.com.mobicare.cielo.commons.data.repository.local

import br.com.mobicare.cielo.commons.data.dataSource.local.ConfigurationLocalDataSource
import br.com.mobicare.cielo.commons.domain.repository.local.ConfigurationLocalRepository

class ConfigurationLocalRepositoryImpl(private val localDataSource: ConfigurationLocalDataSource) :
    ConfigurationLocalRepository {

    override suspend fun getConfiguration(key: String, default: String) =
        localDataSource.getConfigurationValuePreference(key, default)

}