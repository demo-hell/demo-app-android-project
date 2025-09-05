package br.com.mobicare.cielo.technicalSupport.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.technicalSupport.data.dataSource.PredictiveDateSourceImpl
import br.com.mobicare.cielo.technicalSupport.data.dataSource.remote.TechnicalSupportAPI
import br.com.mobicare.cielo.technicalSupport.data.repository.PredictiveBatteryRepositoryImpl
import br.com.mobicare.cielo.technicalSupport.domain.dataSource.PredictiveBatteryDataSource
import br.com.mobicare.cielo.technicalSupport.domain.repository.PredictiveBatteryRepository
import br.com.mobicare.cielo.technicalSupport.domain.useCase.PostChangeBatteryUseCase
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.analytics.PredictiveBatteryAnalytics
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.viewModel.PredictiveBatteryViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule =
    module {
        viewModel { PredictiveBatteryViewModel(get(), get(), get()) }
    }

val analyticsModule =
    module {
        factory { PredictiveBatteryAnalytics() }
    }

val useCaseModule =
    module {
        factory { PostChangeBatteryUseCase(get()) }
    }

val repositoryModule =
    module {
        factory<PredictiveBatteryRepository> {
            PredictiveBatteryRepositoryImpl(get())
        }
    }

val dataSourceModule =
    module {
        factory<PredictiveBatteryDataSource> {
            PredictiveDateSourceImpl(get(), get())
        }
    }

val technicalSupportAPI =
    module {
        factory("technicalSupportAPI") {
            createCieloService(TechnicalSupportAPI::class.java, BuildConfig.HOST_API, get())
        }
    }

val technicalSupportModulesList =
    listOf(
        viewModelModule,
        analyticsModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        technicalSupportAPI,
    )
