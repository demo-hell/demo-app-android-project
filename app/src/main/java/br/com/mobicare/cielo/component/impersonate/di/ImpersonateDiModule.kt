package br.com.mobicare.cielo.component.impersonate.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.component.impersonate.data.datasource.ImpersonateDataSourceImpl
import br.com.mobicare.cielo.component.impersonate.data.datasource.remote.ImpersonateServiceAPI
import br.com.mobicare.cielo.component.impersonate.data.repository.ImpersonateRepositoryImpl
import br.com.mobicare.cielo.component.impersonate.domain.datasource.ImpersonateDataSource
import br.com.mobicare.cielo.component.impersonate.domain.repository.ImpersonateRepository
import br.com.mobicare.cielo.component.impersonate.domain.usecase.PostImpersonateUseCase
import br.com.mobicare.cielo.component.impersonate.presentation.viewModel.ImpersonateViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { ImpersonateViewModel(get(), get(), get(), get(), get(), get()) }
}

val useCaseModule = module {
    factory { PostImpersonateUseCase(get()) }
}

val repositoryModule = module {
    factory<ImpersonateRepository> { ImpersonateRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory<ImpersonateDataSource> { ImpersonateDataSourceImpl(get(), get()) }
}

val apiModule = module {
    factory("impersonatingAPI") {
        createCieloService(ImpersonateServiceAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val impersonatingModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, apiModule
)