package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.dataSource.AccessTokenDataSource
import br.com.mobicare.cielo.commons.data.repository.AccessTokenRepositoryImpl
import br.com.mobicare.cielo.commons.domain.repository.AccessTokenRepository
import br.com.mobicare.cielo.commons.domain.useCase.GetAccessTokenUseCase
import org.koin.dsl.module.module

val accessTokenUseCaseModule = module {
    factory { GetAccessTokenUseCase(get()) }
}

val accessTokenRepositoryModule = module {
    factory<AccessTokenRepository> { AccessTokenRepositoryImpl(get()) }
}

val accessTokenDataSourceModule = module {
    factory { AccessTokenDataSource(UserPreferences.getInstance()) }
}

val accessTokenModulesList = listOf(
    accessTokenUseCaseModule, accessTokenRepositoryModule, accessTokenDataSourceModule
)