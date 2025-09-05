package br.com.mobicare.cielo.home.presentation.postecipado.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.PostecipadoRemoteDataSource
import br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.remote.PostecipadoServerApi
import br.com.mobicare.cielo.home.presentation.postecipado.data.repository.PostecipadoSummaryRepositoryImpl
import br.com.mobicare.cielo.home.presentation.postecipado.domain.repository.PostecipadoSummaryRepository
import br.com.mobicare.cielo.home.presentation.postecipado.domain.usecase.GetPostecipadoSummaryUseCase
import br.com.mobicare.cielo.home.presentation.postecipado.presentation.PostecipadoHomeSummaryViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { PostecipadoHomeSummaryViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { GetPostecipadoSummaryUseCase(get()) }
}

val repositoryModule = module {
    factory { PostecipadoSummaryRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { PostecipadoRemoteDataSource(get(), get()) }
}

val postecipadoApi = module {
    factory("postecipadoAPI")  {
        createCieloService(PostecipadoServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val postecipadoModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, postecipadoApi
)