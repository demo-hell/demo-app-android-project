package br.com.mobicare.cielo.component.requiredDataField.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.component.requiredDataField.data.datasource.RequiredDataFieldDataSourceImpl
import br.com.mobicare.cielo.component.requiredDataField.data.datasource.remote.RequiredDataFieldServiceAPI
import br.com.mobicare.cielo.component.requiredDataField.data.repository.RequiredDataFieldRepositoryImpl
import br.com.mobicare.cielo.component.requiredDataField.domain.datasource.RequiredDataFieldDataSource
import br.com.mobicare.cielo.component.requiredDataField.domain.repository.RequiredDataFieldRepository
import br.com.mobicare.cielo.component.requiredDataField.domain.useCase.PostUpdateDataRequiredDataFieldUseCase
import br.com.mobicare.cielo.component.requiredDataField.presentation.viewmodel.RequiredDataFieldViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { RequiredDataFieldViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { PostUpdateDataRequiredDataFieldUseCase(get()) }
}

val repositoryModule = module {
    factory<RequiredDataFieldRepository> { RequiredDataFieldRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory<RequiredDataFieldDataSource> { RequiredDataFieldDataSourceImpl(get(), get()) }
}

val apiModule = module {
    factory("requiredDataFieldAPI") {
        createCieloService(RequiredDataFieldServiceAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val requiredDataFieldModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, apiModule
)
