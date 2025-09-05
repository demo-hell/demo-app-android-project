package br.com.mobicare.cielo.newRecebaRapido.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote.ReceiveAutomaticApi
import br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote.ReceiveAutomaticRemoteDataSource
import br.com.mobicare.cielo.newRecebaRapido.data.repository.ReceiveAutomaticEligibilityRepositoryImpl
import br.com.mobicare.cielo.newRecebaRapido.data.repository.ReceiveAutomaticOffersRepositoryImpl
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticEligibilityRepository
import br.com.mobicare.cielo.newRecebaRapido.domain.repository.ReceiveAutomaticOffersRepository
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.ContractReceiveAutomaticOfferUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticEligibilityUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.MigrationOfferViewModel
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.RaD1MigrationEffectiveTimeViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.details.ReceiveAutomaticConfirmationViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.details.ReceiveAutomaticDetailsViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.home.ReceiveAutomaticHomeViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.onboarding.ReceiveAutomaticOnBoardingViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.router.ReceiveAutomaticRouterViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { ReceiveAutomaticOnBoardingViewModel(get()) }
    viewModel { ReceiveAutomaticRouterViewModel(get(), get())  }
    viewModel { ReceiveAutomaticHomeViewModel(get(), get()) }
    viewModel { ReceiveAutomaticDetailsViewModel(get(), get()) }
    viewModel { ReceiveAutomaticConfirmationViewModel(get(), get(), get()) }
    viewModel { MigrationOfferViewModel(get(), get()) }
    viewModel { RaD1MigrationEffectiveTimeViewModel(get()) }

}

val useCaseModule = module {
    factory { GetReceiveAutomaticOffersUseCase(get()) }
    factory { ContractReceiveAutomaticOfferUseCase(get()) }
    factory { GetReceiveAutomaticEligibilityUseCase(get()) }

}

val repositoryModule = module {
    factory<ReceiveAutomaticOffersRepository> { ReceiveAutomaticOffersRepositoryImpl(get()) }
    factory<ReceiveAutomaticEligibilityRepository> { ReceiveAutomaticEligibilityRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { ReceiveAutomaticRemoteDataSource(get(), get()) }

}

val receiveAutomaticApi = module {
    factory("receiveAutomaticAPI") {
        createCieloService(ReceiveAutomaticApi::class.java, BuildConfig.HOST_API, get())
    }
}

val RAGA4Module = module {
    factory(name = "RAGA4") {
        RAGA4()
    }
}

val receiveAutomaticModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, receiveAutomaticApi, RAGA4Module
)