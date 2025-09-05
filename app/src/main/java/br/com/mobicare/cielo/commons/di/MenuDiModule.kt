package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.clients.remote.MenuServiceAPI
import br.com.mobicare.cielo.commons.data.dataSource.local.MenuLocalDataSourceImpl
import br.com.mobicare.cielo.commons.data.dataSource.remote.MenuRemoteDataSourceImpl
import br.com.mobicare.cielo.commons.data.repository.remote.MenuRepositoryImpl
import br.com.mobicare.cielo.commons.domain.datasource.MenuLocalDataSource
import br.com.mobicare.cielo.commons.domain.datasource.MenuRemoteDataSource
import br.com.mobicare.cielo.commons.domain.repository.remote.MenuRepository
import br.com.mobicare.cielo.commons.domain.useCase.GetMenuUseCase
import org.koin.dsl.module.module

val menuUseCaseModule = module {
    factory { GetMenuUseCase(get()) }
}

val menuRepositoryModule = module {
    factory<MenuRepository> { MenuRepositoryImpl(get(), get()) }
}

val menuDataSource = module {
    factory<MenuRemoteDataSource> { MenuRemoteDataSourceImpl(get(), get()) }
    factory<MenuLocalDataSource> { MenuLocalDataSourceImpl(UserPreferences.getInstance()) }
}

val menuApiModule = module {
    factory("menuAPI") {
        createCieloService(MenuServiceAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val menuModulesList = listOf(
    menuUseCaseModule, menuRepositoryModule, menuDataSource, menuApiModule
)