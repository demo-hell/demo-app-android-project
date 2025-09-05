package br.com.mobicare.cielo.login.firstAccess.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics
import br.com.mobicare.cielo.login.firstAccess.data.datasource.FirstAccessDataSourceImpl
import br.com.mobicare.cielo.login.firstAccess.data.datasource.remote.FirstAccessServerApi
import br.com.mobicare.cielo.login.firstAccess.data.repository.FirstAccessRepositoryImpl
import br.com.mobicare.cielo.login.firstAccess.domain.repository.FirstAccessRepository
import br.com.mobicare.cielo.login.firstAccess.domain.usecase.FirstAccessRegistrationUseCase
import br.com.mobicare.cielo.login.firstAccess.presentation.ui.createPassword.FirstAccessCreatePasswordViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { FirstAccessCreatePasswordViewModel(get()) }
}

val firstAccessUseCase = module {
    factory { FirstAccessRegistrationUseCase(get()) }
}

val firstAccessRepository = module {
    factory <FirstAccessRepository> { FirstAccessRepositoryImpl(get())}
}

val firstAccessDataSource = module {
    factory { FirstAccessDataSourceImpl(get(), get()) }
}

val firstAccessAnalytics = module {
    factory { FirstAccessAnalytics() }
}

val firstAccessApi = module {
    factory ( "firstAccessApi"){
        createCieloService(FirstAccessServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val firstAccessModulesList = listOf(
    viewModelModule,
    firstAccessUseCase,
    firstAccessRepository,
    firstAccessDataSource,
    firstAccessAnalytics,
    firstAccessApi
)