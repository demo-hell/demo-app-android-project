package br.com.mobicare.cielo.superlink.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.data.datasource.SuperLinkRemoteDataSourceImpl
import br.com.mobicare.cielo.superlink.data.datasource.remote.SuperLinkServiceApi
import br.com.mobicare.cielo.superlink.data.repository.SuperLinkRepositoryImpl
import br.com.mobicare.cielo.superlink.domain.datasource.SuperLinkDataSource
import br.com.mobicare.cielo.superlink.domain.repository.SuperLinkRepository
import br.com.mobicare.cielo.superlink.domain.usecase.CheckPaymentLinkActiveUseCase
import br.com.mobicare.cielo.superlink.presentation.viewmodel.SuperLinkViewModel
import br.com.mobicare.cielo.superlink.utils.SuperLinkNavStartRouter
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { SuperLinkViewModel(get(), get(), get()) }
}

val analyticsModule = module {
    factory { PaymentLinkGA4() }
}

val useCaseModule = module {
    factory { CheckPaymentLinkActiveUseCase(get()) }
}

val repositoryModule = module {
    factory<SuperLinkRepository> { SuperLinkRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory<SuperLinkDataSource> { SuperLinkRemoteDataSourceImpl(get(), get()) }
}

val apiModule = module {
    factory("superLinkAPI") {
        createCieloService(SuperLinkServiceApi::class.java, BuildConfig.HOST_API, get())
    }
}

val utilsModule = module {
    factory { SuperLinkNavStartRouter() }
}

val superLinkModulesList = listOf(
    viewModelModule,
    analyticsModule,
    useCaseModule,
    repositoryModule,
    dataSourceModule,
    apiModule,
    utilsModule
)