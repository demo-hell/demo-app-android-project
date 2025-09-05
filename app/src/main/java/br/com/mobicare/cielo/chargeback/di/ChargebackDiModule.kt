package br.com.mobicare.cielo.chargeback.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackAcceptRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRefuseRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteFiltersDataSource
import br.com.mobicare.cielo.chargeback.data.datasource.remote.ChargebackServerApi
import br.com.mobicare.cielo.chargeback.data.repository.ChargebackAcceptRepositoryImp
import br.com.mobicare.cielo.chargeback.data.repository.ChargebackFiltersRepositoryImpl
import br.com.mobicare.cielo.chargeback.data.repository.ChargebackRefuseRepositoryImp
import br.com.mobicare.cielo.chargeback.data.repository.ChargebackRepositoryImpl
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackAcceptRepository
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackFiltersRepository
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRefuseRepository
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDescriptionReasonUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentSenderUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackFiltersUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackLifecycleUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackPendingUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackTreatedUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.PutChargebackAcceptUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.PutChargebackRefuseUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentSenderViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDoneDetailsViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackFeatureToggleViewModel
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackPendingDetailsViewModel
import br.com.mobicare.cielo.chargeback.presentation.filters.ChargebackFiltersViewModel
import br.com.mobicare.cielo.chargeback.presentation.home.ChargebackHomeViewModel
import br.com.mobicare.cielo.chargeback.presentation.refuse.ChargebackRefuseViewModel
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { ChargebackHomeViewModel(get(), get(), get()) }
    viewModel { ChargebackPendingDetailsViewModel(get(), get()) }
    viewModel { ChargebackDoneDetailsViewModel(get()) }
    viewModel { ChargebackDocumentViewModel(get()) }
    viewModel { ChargebackRefuseViewModel(get(), get()) }
    viewModel { ChargebackFeatureToggleViewModel(get(),get()) }
    viewModel { ChargebackFiltersViewModel(get()) }
    viewModel { ChargebackDocumentSenderViewModel(get()) }
}

val useCaseModule = module {
    factory { GetChargebackPendingUseCase(get()) }
    factory { GetChargebackTreatedUseCase(get()) }
    factory { GetChargebackLifecycleUseCase(get()) }
    factory { GetChargebackDocumentUseCase(get()) }
    factory { PutChargebackRefuseUseCase(get()) }
    factory { PutChargebackAcceptUseCase(get()) }
    factory { GetChargebackDescriptionReasonUseCase(get()) }
    factory { GetChargebackFiltersUseCase(get()) }
    factory { GetChargebackDocumentSenderUseCase(get()) }
}

val repositoryModule = module {
    factory<ChargebackRepository> { ChargebackRepositoryImpl(get()) }
    factory<ChargebackRefuseRepository> { ChargebackRefuseRepositoryImp(get()) }
    factory<ChargebackAcceptRepository> { ChargebackAcceptRepositoryImp(get()) }
    factory<ChargebackFiltersRepository> { ChargebackFiltersRepositoryImpl(get())  }
}

val dataSourceModule = module {
    factory { ChargebackRemoteDataSource(get(), get()) }
    factory { ChargebackRefuseRemoteDataSource(get(), get()) }
    factory { ChargebackAcceptRemoteDataSource(get(), get()) }
    factory { ChargebackRemoteFiltersDataSource(get(),get()) }
}

val chargebackApi = module {
    factory("chargebackAPI") {
        createCieloService(ChargebackServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val chargebackGA4 = module {
    factory(name = "chargebackGA4") {
        ChargebackGA4()
    }
}

val chargebackModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, chargebackApi, chargebackGA4
)