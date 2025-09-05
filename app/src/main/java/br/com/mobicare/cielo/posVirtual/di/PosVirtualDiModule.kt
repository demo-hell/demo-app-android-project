package br.com.mobicare.cielo.posVirtual.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualAccreditationDataSource
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualEligibilityDataSource
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualQRCodePixDataSource
import br.com.mobicare.cielo.posVirtual.data.dataSource.remote.PosVirtualAPI
import br.com.mobicare.cielo.posVirtual.data.repository.PosVirtualAccreditationRepositoryImpl
import br.com.mobicare.cielo.posVirtual.data.repository.PosVirtualEligibilityRepositoryImpl
import br.com.mobicare.cielo.posVirtual.data.repository.PosVirtualQRCodePixRepositoryImpl
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualEligibilityRepository
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualQRCodePixRepository
import br.com.mobicare.cielo.posVirtual.domain.useCase.*
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire.PosVirtualAccreditationHireViewModel
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.PosVirtualAccreditationOfferViewModel
import br.com.mobicare.cielo.posVirtual.presentation.home.PosVirtualHomeViewModel
import br.com.mobicare.cielo.posVirtual.presentation.qrCodePix.insertAmount.PosVirtualQRCodePixInsertAmountViewModel
import br.com.mobicare.cielo.posVirtual.presentation.router.PosVirtualRouterViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { PosVirtualRouterViewModel(get(), get(), get()) }
    viewModel { PosVirtualHomeViewModel() }
    viewModel { PosVirtualAccreditationOfferViewModel(get(), get()) }
    viewModel { PosVirtualAccreditationHireViewModel(get(), get(), get(), get()) }
    viewModel { PosVirtualQRCodePixInsertAmountViewModel(get(), get()) }
}

val analyticsModule = module {
    factory { PosVirtualAnalytics() }
}

val useCaseModule = module {
    factory { GetPosVirtualEligibilityUseCase(get()) }
    factory { GetPosVirtualAccreditationBanksUseCase(get()) }
    factory { GetPosVirtualAccreditationOffersUseCase(get()) }
    factory { PostPosVirtualCreateOrderUseCase(get()) }
    factory { PostPosVirtualCreateQRCodePixUseCase(get()) }
}

val repositoryModule = module {
    factory<PosVirtualEligibilityRepository> {
        PosVirtualEligibilityRepositoryImpl(get())
    }

    factory<PosVirtualQRCodePixRepository> {
        PosVirtualQRCodePixRepositoryImpl(get())
    }

    factory<PosVirtualAccreditationRepository> {
        PosVirtualAccreditationRepositoryImpl(get())
    }
}

val dataSourceModule = module {
    factory { PosVirtualEligibilityDataSource(get(), get()) }
    factory { PosVirtualQRCodePixDataSource(get(), get()) }
    factory { PosVirtualAccreditationDataSource(get(), get()) }
}

val posVirtualApi = module {
    factory("posVirtualAPI") {
        createCieloService(PosVirtualAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val posVirtualModuleList = listOf(
    viewModelModule,
    analyticsModule,
    useCaseModule,
    repositoryModule,
    dataSourceModule,
    posVirtualApi
)