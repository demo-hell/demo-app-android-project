package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.dataSource.local.UserPreferencesLocalDataSource
import br.com.mobicare.cielo.commons.data.repository.local.UserPreferencesLocalRepositoryImpl
import br.com.mobicare.cielo.commons.domain.repository.local.UserPreferencesLocalRepository
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.DeleteUserPreferencesUseCase
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.GetUserPreferencesUseCase
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.PutUserPreferencesUseCase
import org.koin.dsl.module.module

val userPreferencesModule = module {
    factory { UserPreferences.getInstance() }
}

val userPreferencesUseCaseModule = module {
    factory { GetUserPreferencesUseCase(get()) }
    factory { PutUserPreferencesUseCase(get()) }
    factory { DeleteUserPreferencesUseCase(get()) }
}

val userPreferencesRepositoryModule = module {
    factory<UserPreferencesLocalRepository> { UserPreferencesLocalRepositoryImpl(get()) }
}

val userPreferencesLocalDataSourceModule = module {
    factory {
        UserPreferencesLocalDataSource( UserPreferences.getInstance() )
    }
}

val userPreferencesModulesList = listOf(
    userPreferencesModule,
    userPreferencesUseCaseModule,
    userPreferencesRepositoryModule,
    userPreferencesLocalDataSourceModule
)