package br.com.mobicare.cielo.eventTracking.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.eventTracking.data.datasource.CallsEventRemoteDataSource
import br.com.mobicare.cielo.eventTracking.data.datasource.RequestDeliveryEventRemoteDatasource
import br.com.mobicare.cielo.eventTracking.data.datasource.api.TrackEventApi
import br.com.mobicare.cielo.eventTracking.data.repository.CallsEventRepositoryImpl
import br.com.mobicare.cielo.eventTracking.data.repository.DeliveryEventRepositoryImpl
import br.com.mobicare.cielo.eventTracking.domain.repository.CallsEventRepository
import br.com.mobicare.cielo.eventTracking.domain.repository.DeliveryEventRepository
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetCallsEventListUseCase
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetDeliveryEventListUseCase
import br.com.mobicare.cielo.eventTracking.presentation.analytics.CallsTrackingGA4
import br.com.mobicare.cielo.eventTracking.presentation.analytics.EventTrackingGA4
import br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest.CallsRequestViewModel
import br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest.MachineRequestViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { MachineRequestViewModel(get()) }
    viewModel { CallsRequestViewModel(get()) }
}

val useCaseModule = module {
    factory { GetDeliveryEventListUseCase(get()) }
    factory { GetCallsEventListUseCase(get()) }
}

val repositoryModule = module {
    factory<DeliveryEventRepository> { DeliveryEventRepositoryImpl(get()) }
    factory<CallsEventRepository> { CallsEventRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { RequestDeliveryEventRemoteDatasource(get(), get()) }
    factory { CallsEventRemoteDataSource(get(), get()) }
}

val eventTrackingAPI = module {
    factory("eventTrackingAPI") {
        createCieloService(TrackEventApi::class.java, BuildConfig.HOST_API, get())
    }
}

val analyticsModule = module {
    single { EventTrackingGA4() }
    single { CallsTrackingGA4() }
}

val eventTrackingModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, eventTrackingAPI, analyticsModule
)