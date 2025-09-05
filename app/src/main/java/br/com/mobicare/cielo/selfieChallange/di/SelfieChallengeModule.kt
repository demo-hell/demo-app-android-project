package br.com.mobicare.cielo.selfieChallange.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.selfieChallange.data.datasource.remote.SelfieChallengeApi
import br.com.mobicare.cielo.selfieChallange.data.datasource.remote.SelfieChallengeRemoteDataSource
import br.com.mobicare.cielo.selfieChallange.data.repository.SelfieChallengeRepositoryImpl
import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository
import br.com.mobicare.cielo.selfieChallange.domain.usecase.GetStoneAgeTokenUseCase
import br.com.mobicare.cielo.selfieChallange.domain.usecase.PostSelfieChallengeUseCase
import br.com.mobicare.cielo.selfieChallange.presentation.SelfieChallengeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModule = module {
    viewModel { SelfieChallengeViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { GetStoneAgeTokenUseCase(get()) }
    factory { PostSelfieChallengeUseCase(get()) }
}

val repositoryModule = module {
    factory<SelfieChallengeRepository> { SelfieChallengeRepositoryImpl(get()) }
}

val remoteDataSourceModule = module {
    factory { SelfieChallengeRemoteDataSource(get(), get()) }
}

val apiModule = module {
    factory("selfieChallengeApi") {
        createCieloService(
            SelfieChallengeApi::class.java,
            BuildConfig.HOST_API,
            get()
        )
    }
}

val datadogEvent = module {
    factory("datadogEvent") {
        DatadogEvent(androidContext(), UserPreferences.getInstance())
    }
}

val selfieChallengeModule = listOf(
    viewModule,
    useCaseModule,
    repositoryModule,
    remoteDataSourceModule,
    apiModule,
    datadogEvent
)