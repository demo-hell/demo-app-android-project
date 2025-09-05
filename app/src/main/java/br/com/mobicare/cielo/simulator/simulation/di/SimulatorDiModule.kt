package br.com.mobicare.cielo.simulator.simulation.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.simulator.analytics.SalesSimulatorGA4
import br.com.mobicare.cielo.simulator.simulation.data.datasource.remote.SimulatorApi
import br.com.mobicare.cielo.simulator.simulation.data.datasource.remote.SimulatorRemoteDataSource
import br.com.mobicare.cielo.simulator.simulation.data.repository.SimulatorRepositoryImpl
import br.com.mobicare.cielo.simulator.simulation.domain.repository.SimulatorRepository
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulationUseCase
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulatorProductsUseCase
import br.com.mobicare.cielo.simulator.simulation.presentation.result.SaleSimulatorReceiveTotalValueViewModel
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

private val viewModelModule =
    module {
        viewModel { SimulatorViewModel(get(), get(), get()) }
        viewModel { SaleSimulatorReceiveTotalValueViewModel(get()) }
    }

private val useCaseModule =
    module {
        factory<GetSimulatorProductsUseCase> { GetSimulatorProductsUseCase(get()) }
        factory<GetSimulationUseCase> { GetSimulationUseCase(get()) }
    }

private val repositoryModule =
    module {
        factory<SimulatorRepository> { SimulatorRepositoryImpl(get()) }
    }

private val dataSourceModule =
    module {
        factory { SimulatorRemoteDataSource(get(), get()) }
    }

private val mdrServerApi =
    module {
        factory("simulatorApi") {
            createCieloService(SimulatorApi::class.java, BuildConfig.HOST_API, get())
        }
    }

private val analyticsModule =
    module {
        single {
            SalesSimulatorGA4()
        }
    }

val simulatorModulesList =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        mdrServerApi,
        analyticsModule
    )
