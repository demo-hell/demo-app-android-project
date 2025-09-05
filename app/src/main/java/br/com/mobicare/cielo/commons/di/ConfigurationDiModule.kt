package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.commons.data.dataSource.local.ConfigurationLocalDataSource
import br.com.mobicare.cielo.commons.data.repository.local.ConfigurationLocalRepositoryImpl
import br.com.mobicare.cielo.commons.domain.repository.local.ConfigurationLocalRepository
import br.com.mobicare.cielo.commons.domain.useCase.GetConfigurationUseCase
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import org.koin.dsl.module.module

val configurationUseCaseModule = module {
    factory { GetConfigurationUseCase(get()) }
}

val configurationRepositoryModule = module {
    factory<ConfigurationLocalRepository> { ConfigurationLocalRepositoryImpl(get()) }
}

val configurationDataSourceModule = module {
    factory {
        ConfigurationLocalDataSource(
            ConfigurationPreference.instance
        )
    }
}

val configurationModulesList = listOf(
    configurationUseCaseModule,
    configurationRepositoryModule,
    configurationDataSourceModule
)
