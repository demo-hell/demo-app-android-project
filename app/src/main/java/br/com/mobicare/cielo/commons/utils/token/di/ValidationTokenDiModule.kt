package br.com.mobicare.cielo.commons.utils.token.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.token.data.dataSource.TokenDataSource
import br.com.mobicare.cielo.commons.utils.token.data.repository.TokenRepositoryImpl
import br.com.mobicare.cielo.commons.utils.token.domain.repository.TokenRepository
import br.com.mobicare.cielo.commons.utils.token.domain.useCase.GetTokenUseCase
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationTokenViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val handlerModule = module {
    factory { HandlerValidationToken() }
}

val viewModelModule = module {
    viewModel { HandlerValidationTokenViewModel(get()) }
}

val useCaseModule = module {
    factory { GetTokenUseCase(get()) }
}

val repositoryModule = module {
    factory<TokenRepository> { TokenRepositoryImpl(get()) }
}

val mfaUserInformation = module {
    factory {
        MfaUserInformation(get())
    }
}

val dataSourceModule = module {
    factory {
        TokenDataSource(
            get(),
            get(),
            UserPreferences.getInstance()
        )
    }
}

val tokenModulesList = listOf(
    repositoryModule,
    handlerModule,
    viewModelModule,
    useCaseModule,
    dataSourceModule,
    mfaUserInformation
)