package br.com.mobicare.cielo.cancelSale.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.cancelSale.data.datasource.BalanceInquiryDataSource
import br.com.mobicare.cielo.cancelSale.data.datasource.CancelSaleDataSource
import br.com.mobicare.cielo.cancelSale.data.datasource.remote.CancelSaleAPI
import br.com.mobicare.cielo.cancelSale.data.repository.BalanceInquiryRepositoryImpl
import br.com.mobicare.cielo.cancelSale.data.repository.CancelSaleRepositoryImpl
import br.com.mobicare.cielo.cancelSale.domain.repository.BalanceInquiryRemoteRepository
import br.com.mobicare.cielo.cancelSale.domain.repository.CancelSaleRemoteRepository
import br.com.mobicare.cielo.cancelSale.domain.usecase.BalanceInquiryUseCase
import br.com.mobicare.cielo.cancelSale.domain.usecase.CancelSaleUseCase
import br.com.mobicare.cielo.cancelSale.presentation.detail.DetailCancelSaleViewModel
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { DetailCancelSaleViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { BalanceInquiryUseCase(get()) }
    factory { CancelSaleUseCase(get()) }
}

val repositoryModule = module {
    factory<BalanceInquiryRemoteRepository> { BalanceInquiryRepositoryImpl(get()) }
    factory<CancelSaleRemoteRepository> { CancelSaleRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { BalanceInquiryDataSource(get(), get()) }
    factory { CancelSaleDataSource(get(), get()) }
}

val apiModule = module {
    factory("cancelSaleAPI") {
        createCieloService(CancelSaleAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val cancelSaleModuleList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, apiModule
)