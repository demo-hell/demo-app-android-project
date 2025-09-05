package br.com.mobicare.cielo.interactBannersOffersNew.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.interactBannersOffersNew.analytics.InteractBannersNewAnalytics
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.InteractBannerNewRemoteDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.local.InteractBannerNewLocalDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.remote.InteractBannerServerAPI
import br.com.mobicare.cielo.interactBannersOffersNew.data.repository.InteractBannerNewRepositoryImpl
import br.com.mobicare.cielo.interactBannersOffersNew.domain.repository.InteractBannerNewRepository
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.DeleteLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetRemoteInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.SaveLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.presentation.InteractBannersViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val interactBannersViewModelModule = module {
    viewModel { InteractBannersViewModel(get(), get(), get(), get(), get()) }
}

val interactBannersUseCaseModule = module {
    factory { GetRemoteInteractBannersOffersUseCase(get()) }
    factory { GetLocalInteractBannersOffersUseCase(get()) }
    factory { SaveLocalInteractBannersOffersUseCase(get()) }
    factory { DeleteLocalInteractBannersOffersUseCase(get()) }
}

val interactBannersAnalytics = module {
    factory(name = "InteractBannersNewAnalytics") {
        InteractBannersNewAnalytics()
    }
}

val interactBannersRepositoryModule = module {
    factory<InteractBannerNewRepository> { InteractBannerNewRepositoryImpl(get(), get()) }
}

val interactBannersDataSourceModule = module {
    factory { InteractBannerNewLocalDataSource(get()) }
    factory { InteractBannerNewRemoteDataSource(get(), get(), get()) }
}

val interactBannersAPIModule = module {
    factory("InteractBannersAPI"){
        createCieloService(InteractBannerServerAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val interactBannersModulesList = listOf(
    interactBannersViewModelModule,
    interactBannersUseCaseModule,
    interactBannersRepositoryModule,
    interactBannersDataSourceModule,
    interactBannersAPIModule,
    interactBannersAnalytics
)