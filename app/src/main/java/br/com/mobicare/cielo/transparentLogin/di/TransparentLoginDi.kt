package br.com.mobicare.cielo.transparentLogin.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4
import br.com.mobicare.cielo.transparentLogin.data.TransparentLoginRemoteDataSource
import br.com.mobicare.cielo.transparentLogin.data.remote.TransparentLoginServerApi
import br.com.mobicare.cielo.transparentLogin.data.repository.TransparentLoginRepositoryImpl
import br.com.mobicare.cielo.transparentLogin.domain.repository.TransparentLoginRepository
import br.com.mobicare.cielo.transparentLogin.domain.useCase.PostTransparentLoginUseCase
import br.com.mobicare.cielo.transparentLogin.presentation.TransparentLoginViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule =
    module {
        viewModel { TransparentLoginViewModel(get(), get(), get(), get(), get()) }
    }

val useCaseModule =
    module {
        factory { PostTransparentLoginUseCase(get()) }
    }

val repositoryModule =
    module {
        factory<TransparentLoginRepository> { TransparentLoginRepositoryImpl(get()) }
    }

val dataSourceModule =
    module {
        factory { TransparentLoginRemoteDataSource(get(), get()) }
    }

val transparentLoginGA4 =
    module {
        factory(name = "transparentLoginGA4") {
            TransparentLoginGA4()
        }
    }

val transparentLoginServerApi =
    module {
        factory("transparentLoginServerApi") {
            createCieloService(TransparentLoginServerApi::class.java, BuildConfig.HOST_API, get())
        }
    }

val transparentLoginModulesList =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        transparentLoginGA4,
        transparentLoginServerApi,
    )
