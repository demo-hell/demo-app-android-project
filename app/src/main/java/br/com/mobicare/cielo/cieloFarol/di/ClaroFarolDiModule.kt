package br.com.mobicare.cielo.cieloFarol.di

import org.koin.androidx.viewmodel.ext.koin.viewModel
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.cieloFarol.domain.repository.CieloFarolRepository
import br.com.mobicare.cielo.cieloFarol.data.dataSource.CieloFarolDataSource
import br.com.mobicare.cielo.cieloFarol.data.dataSource.remote.CieloFarolServerApi
import br.com.mobicare.cielo.cieloFarol.data.repository.CieloFarolRepositoryImpl
import br.com.mobicare.cielo.cieloFarol.domain.useCase.GetCieloFarolUseCase
import br.com.mobicare.cielo.cieloFarol.presentation.CieloFarolViewModel
import br.com.mobicare.cielo.cieloFarol.utils.analytics.CieloFarolAnalytics
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { CieloFarolViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { GetCieloFarolUseCase(get()) }
}

val repositoryModule = module {
    factory<CieloFarolRepository> { CieloFarolRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { CieloFarolDataSource(get(), get()) }
}

val cieloFarolApi = module {
    factory ("cieloFarolApi") {
        createCieloService(CieloFarolServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val cieloFarolAnalytics = module {
    factory(name = "CieloFarolAnalytics") {
        CieloFarolAnalytics()
    }
}

val cieloFarolModulesList = listOf(viewModelModule, useCaseModule, repositoryModule, dataSourceModule, cieloFarolApi, cieloFarolAnalytics)