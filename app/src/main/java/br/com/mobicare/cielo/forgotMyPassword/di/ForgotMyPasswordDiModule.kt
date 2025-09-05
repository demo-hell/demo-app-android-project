package br.com.mobicare.cielo.forgotMyPassword.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4
import br.com.mobicare.cielo.forgotMyPassword.data.dataSource.ForgotMyPasswordRemoteDataSource
import br.com.mobicare.cielo.forgotMyPassword.data.dataSource.remote.ForgotMyPasswordServerAPI
import br.com.mobicare.cielo.forgotMyPassword.data.repository.ForgotMyPasswordRepositoryImpl
import br.com.mobicare.cielo.forgotMyPassword.domain.repository.ForgotMyPasswordRepository
import br.com.mobicare.cielo.forgotMyPassword.domain.useCase.PostForgotMyPasswordRecoveryPasswordUseCase
import br.com.mobicare.cielo.forgotMyPassword.presentation.insertInfo.ForgotMyPasswordInsertInfoViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { ForgotMyPasswordInsertInfoViewModel(get(), UserPreferences.getInstance()) }
}

val useCaseModule = module {
    factory { PostForgotMyPasswordRecoveryPasswordUseCase(get()) }
}

val repositoryModule = module {
    factory<ForgotMyPasswordRepository> { ForgotMyPasswordRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { ForgotMyPasswordRemoteDataSource(get(), get()) }
}

val forgotMyPasswordAPI = module {
    factory("ForgotMyPasswordAPI") {
        createCieloService(ForgotMyPasswordServerAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val appForgotMyPassword = module {
    factory (name = "forgotMyPasswordGA4"){
        ForgotMyPasswordGA4()
    }
}


val forgotMyPasswordModulesList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, forgotMyPasswordAPI, appForgotMyPassword
)