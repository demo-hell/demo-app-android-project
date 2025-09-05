package br.com.mobicare.cielo.p2m.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics
import br.com.mobicare.cielo.p2m.analytics.P2MGA4
import br.com.mobicare.cielo.p2m.data.datasource.remote.P2mAcceptRemoteDataSource
import br.com.mobicare.cielo.p2m.data.datasource.remote.P2mApi
import br.com.mobicare.cielo.p2m.data.repository.P2mAcceptRepositoryImpl
import br.com.mobicare.cielo.p2m.domain.repository.P2mAcceptRepository
import br.com.mobicare.cielo.p2m.domain.usecase.GetFeatureToggleMessageUseCase
import br.com.mobicare.cielo.p2m.domain.usecase.PutP2mAcceptUseCase
import br.com.mobicare.cielo.p2m.presentation.viewmodel.P2mAcreditationViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { P2mAcreditationViewModel(get(),get(),get()) }

}

val useCaseModule = module {
    factory { PutP2mAcceptUseCase(get()) }
    factory { GetFeatureToggleMessageUseCase(get()) }

}

val repositoryModule = module {
    factory<P2mAcceptRepository> { P2mAcceptRepositoryImpl(get()) }

}

val dataSourceModule = module {
    factory { P2mAcceptRemoteDataSource(get(), get()) }

}

val p2mApi = module {
    factory("p2mAPI") {
        createCieloService(P2mApi::class.java, BuildConfig.HOST_API, get())
    }
}

val p2mAnalytics = module {
    factory(name = "P2mAnalytics") {
        P2MAnalytics()
    }
}

val P2MGA4Module = module {
    factory(name = "P2MGA4") {
        P2MGA4()
    }
}

val p2mModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, p2mApi, p2mAnalytics, P2MGA4Module
)