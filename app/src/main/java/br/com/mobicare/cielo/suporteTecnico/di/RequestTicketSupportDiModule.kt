package br.com.mobicare.cielo.suporteTecnico.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.ProblemEquipmentsDataSource
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.RequestTicketSupportDataSource
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.remote.NewTechnicalSupportAPI
import br.com.mobicare.cielo.suporteTecnico.data.repository.ProblemEquipmentsRepositoryImpl
import br.com.mobicare.cielo.suporteTecnico.data.repository.RequestTicketSupportRepositoryImpl
import br.com.mobicare.cielo.suporteTecnico.domain.repo.ProblemEquipmentsRepository
import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetMerchantEquipmentsUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetPostOrdersReplacementsUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetProblemEquipmentsUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetRequestTicketSupportUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetScheduleAvailabilityUseCase
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.MerchantEquipmentsViewModel
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.ProblemListViewModel
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.RequestTicketSupportViewModel
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.ScheduleAvailabilityViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { RequestTicketSupportViewModel(get()) }
    viewModel { MerchantEquipmentsViewModel(get(), get()) }
    viewModel { ProblemListViewModel(get()) }
    viewModel { ScheduleAvailabilityViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { GetRequestTicketSupportUseCase(get()) }
    factory { GetMerchantEquipmentsUseCase(get()) }
    factory { GetScheduleAvailabilityUseCase(get()) }
    factory { GetPostOrdersReplacementsUseCase(get()) }
    factory { GetProblemEquipmentsUseCase(get()) }
}

val repositoryModule = module {
    factory<RequestTicketSupportRepository> { RequestTicketSupportRepositoryImpl(get()) }
    factory<ProblemEquipmentsRepository> { ProblemEquipmentsRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { RequestTicketSupportDataSource(get(), get()) }
    factory { ProblemEquipmentsDataSource(get(), get()) }
}

val requestTicketSupportApi = module {
    factory("newTechnicalSupportAPI") {
        createCieloService(NewTechnicalSupportAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val requestTicketSupportModulesList = listOf(
    viewModelModule,
    useCaseModule,
    repositoryModule, dataSourceModule, requestTicketSupportApi
)