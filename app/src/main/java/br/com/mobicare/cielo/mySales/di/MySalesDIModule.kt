package br.com.mobicare.cielo.mySales.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesFiltersRemoteDataSource
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesRemoteAPI
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesRemoteDataSource
import br.com.mobicare.cielo.mySales.data.repository.MySalesFiltersRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.repository.MySalesFiltersRemoteRepository
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository
import br.com.mobicare.cielo.mySales.domain.usecase.GetCanceledSalesUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetCardBrandsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetFilteredCanceledSellsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetGA4UseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetHomeCardSummarySalesUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetMySalesTransactionsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetPaymentTypeUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetSaleMerchantUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesHistoryUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesTransactionsViewModel
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HistorySalesViewModel
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesCardViewModel
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesViewModel
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesFiltersViewModel
import br.com.mobicare.cielo.mySales.presentation.viewmodel.SaleDetailsViewModel
import org.koin.dsl.module.module
import org.koin.androidx.viewmodel.ext.koin.viewModel


val viewModelModule = module {
    viewModel { HomeSalesCardViewModel(get(), get() ,get(), get()) }
    viewModel { HomeSalesViewModel(get(),get(),get(),get(), get()) }
    viewModel { HistorySalesViewModel(get(),get(),get()) }
    viewModel { MySalesTransactionsViewModel(get(),get(),get(), get()) }
    viewModel { SaleDetailsViewModel(get(),get(),get(), MenuPreference.instance, get()) }
    viewModel { MySalesFiltersViewModel( get(),get(),get(), get() ) }
}

val useCaseModule = module {
    factory { GetSalesUseCase(get()) }
    factory { GetHomeCardSummarySalesUseCase(get()) }
    factory { GetCanceledSalesUseCase(get()) }
    factory { GetSalesHistoryUseCase(get()) }
    factory { GetMySalesTransactionsUseCase(get()) }
    factory { GetSaleMerchantUseCase( get()) }
    factory { GetPaymentTypeUseCase( get()) }
    factory { GetCardBrandsUseCase( get()) }
    factory { GetFilteredCanceledSellsUseCase( get() ) }
    factory { GetGA4UseCase( get() ) }
}

val repositoryModule = module {
    factory<MySalesRemoteRepository> { MySalesRemoteRepositoryImpl(get())  }
    factory<MySalesFiltersRemoteRepository> { MySalesFiltersRemoteRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { MySalesRemoteDataSource(get(),get()) }
    factory { MySalesFiltersRemoteDataSource(get(),get()) }
}

val apiModule = module {
    factory("mySalesAPI") {
        createCieloService(MySalesRemoteAPI::class.java,BuildConfig.HOST_API,get())
    }
}

val MySalesModuleList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, apiModule
)