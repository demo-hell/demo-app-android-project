package br.com.mobicare.cielo.contactCielo.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.contactCielo.data.datasource.SegmentCodeRemoteSource
import br.com.mobicare.cielo.contactCielo.data.datasource.local.SegmentCodeLocalSource
import br.com.mobicare.cielo.contactCielo.data.datasource.remote.SegmentCodeServerApi
import br.com.mobicare.cielo.contactCielo.data.repository.SegmentCodeRepositoryImpl
import br.com.mobicare.cielo.contactCielo.domain.ContactCieloViewModel
import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetLocalSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetRemoteSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.RemoveLocalSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.SaveLocalSegmentCodeUseCase
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { ContactCieloViewModel(get(), get(), get(), get()) }
}

val useCaseModule = module {
    factory { GetRemoteSegmentCodeUseCase(get()) }
    factory { GetLocalSegmentCodeUseCase(get()) }
    factory { RemoveLocalSegmentCodeUseCase(get()) }
    factory { SaveLocalSegmentCodeUseCase(get()) }
}

val repositoryModule = module {
    factory<SegmentCodeRepository> { SegmentCodeRepositoryImpl(get(), get()) }
}

val datasourceModule = module {
    factory { SegmentCodeRemoteSource(get(), get()) }
    factory { SegmentCodeLocalSource(get()) }
}

val segmentCodeApi = module {
    factory("segmentCodeApi") {
        createCieloService(SegmentCodeServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val contactCieloModulesList =
    listOf(viewModelModule, useCaseModule, repositoryModule, datasourceModule, segmentCodeApi)