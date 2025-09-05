package br.com.mobicare.cielo.mdr.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.mdr.analytics.MdrAnalyticsGA4
import br.com.mobicare.cielo.mdr.data.datasource.MdrRemoteDataSource
import br.com.mobicare.cielo.mdr.data.datasource.MdrServerApi
import br.com.mobicare.cielo.mdr.data.repository.MdrRepositoryImpl
import br.com.mobicare.cielo.mdr.data.usecase.PostContractUseCaseImpl
import br.com.mobicare.cielo.mdr.domain.repository.MdrRepository
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase
import br.com.mobicare.cielo.mdr.ui.MdrOfferViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule =
    module {
        viewModel { MdrOfferViewModel(get(), get()) }
    }

val useCaseModule =
    module {
        factory<PostContractUseCase> { PostContractUseCaseImpl(get()) }
    }

val repositoryModule =
    module {
        factory<MdrRepository> { MdrRepositoryImpl(get()) }
    }

val dataSourceModule =
    module {
        factory { MdrRemoteDataSource(get(), get(), UserPreferences.getInstance()) }
    }

val mdrServerApi =
    module {
        factory("mdrApi") {
            createCieloService(MdrServerApi::class.java, BuildConfig.HOST_API, get())
        }
    }

val analyticsModule =
    module {
        factory { MdrAnalyticsGA4() }
    }

val mdrModulesList =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        mdrServerApi,
        analyticsModule,
    )
