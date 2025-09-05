package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.clients.remote.UserInformationServerApi
import br.com.mobicare.cielo.commons.data.dataSource.local.UserInformationLocalDataSource
import br.com.mobicare.cielo.commons.data.dataSource.remote.UserInformationRemoteDataSource
import br.com.mobicare.cielo.commons.data.repository.local.UserInformationLocalRepositoryImpl
import br.com.mobicare.cielo.commons.data.repository.remote.UserInformationRemoteRepositoryImpl
import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository
import br.com.mobicare.cielo.commons.domain.repository.remote.UserInformationRemoteRepository
import br.com.mobicare.cielo.commons.domain.useCase.DeleteUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import org.koin.dsl.module.module

val userInformationUseCaseModule =
    module {
        factory { GetUserObjUseCase(get()) }
        factory { GetMeInformationUseCase(get(), get()) }
        factory { GetUserViewHistoryUseCase(get()) }
        factory { SaveUserViewHistoryUseCase(get()) }
        factory { DeleteUserViewHistoryUseCase(get()) }
        factory { GetUserViewCounterUseCase(get()) }
        factory { SaveUserViewCounterUseCase(get()) }
    }

val userInformationRepositoryModule =
    module {
        factory<UserInformationLocalRepository> { UserInformationLocalRepositoryImpl(get()) }
        factory<UserInformationRemoteRepository> { UserInformationRemoteRepositoryImpl(get()) }
    }

val userInformationDataSourceModule =
    module {
        factory {
            UserInformationLocalDataSource(
                MenuPreference.instance,
                UserPreferences.getInstance(),
            )
        }

        factory { Analytics.Update }
        factory { UserInformationRemoteDataSource(get(), get(), get(), MenuPreference.instance) }
    }

val userInformationApiModule =
    module {
        factory("userInformationServerApi") {
            createCieloService(
                UserInformationServerApi::class.java,
                BuildConfig.HOST_API,
                get(),
            )
        }
    }

val getUserObjModulesList =
    listOf(
        userInformationUseCaseModule,
        userInformationRepositoryModule,
        userInformationDataSourceModule,
        userInformationApiModule,
    )
