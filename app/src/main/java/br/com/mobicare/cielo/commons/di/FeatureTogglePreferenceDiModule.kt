package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.remote.FeatureToggleServerApi
import br.com.mobicare.cielo.commons.data.dataSource.local.FeatureTogglePreferenceLocalDataSource
import br.com.mobicare.cielo.commons.data.dataSource.remote.FeatureTogglePreferenceRemoteDataSource
import br.com.mobicare.cielo.commons.data.repository.local.FeatureTogglePreferenceRepositoryImpl
import br.com.mobicare.cielo.commons.data.repository.remote.FeatureTogglePreferenceRemoteRepositoryImpl
import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository
import br.com.mobicare.cielo.commons.domain.repository.remote.FeatureTogglePreferenceRemoteRepository
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureToggleUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import org.koin.dsl.module.module

val featureTogglePreferenceUseCaseModule = module {
    factory { GetFeatureTogglePreferenceUseCase(get()) }
    factory { GetFeatureToggleUseCase(get()) }
}

val featureToggleServerApi = module {
        factory("featureToggleServerAPI") {
            createCieloService(
                FeatureToggleServerApi::class.java,
                BuildConfig.HOST_API,
                get()
            )
        }
}

val featureTogglePreferenceDataSourceModule = module {
    factory {
        FeatureTogglePreferenceLocalDataSource(
            get()
        )
    }

    factory { FeatureTogglePreferenceRemoteDataSource(get(), get()) }
}

val featureTogglePreferenceRepositoryModule = module {
    factory<FeatureTogglePreferenceRepository> { FeatureTogglePreferenceRepositoryImpl(get(), get()) }
    factory<FeatureTogglePreferenceRemoteRepository> { FeatureTogglePreferenceRemoteRepositoryImpl(get()) }
}

val featureTogglePreferenceModule = module {
    factory { FeatureTogglePreference.instance }
}

val featureTogglePreferenceModulesList = listOf(
    featureTogglePreferenceUseCaseModule,
    featureTogglePreferenceRepositoryModule,
    featureToggleServerApi,
    featureTogglePreferenceDataSourceModule,
    featureTogglePreferenceModule
)
