package br.com.mobicare.cielo.meuCadastroNovo.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote.MyAccountApi
import br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote.MyAccountRemoteDataSource
import br.com.mobicare.cielo.meuCadastroNovo.data.repository.MyAccountRepositoryImpl
import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.GetAdditionalFieldsInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PostUserValidateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutAdditionalInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutUserUpdateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange.UserAdditionalInfoChangeViewModel
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.userDataChange.UserDataChangeViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModule = module {
    viewModel { UserDataChangeViewModel(get(), get(), get()) }
    viewModel { UserAdditionalInfoChangeViewModel(get(), get()) }
}

val useCaseModule = module {
    factory { PostUserValidateDataUseCase(get()) }
    factory { PutUserUpdateDataUseCase(get()) }
    factory { GetAdditionalFieldsInfoUseCase(get()) }
    factory { PutAdditionalInfoUseCase(get()) }
}

val repositoryModule = module {
    factory<MyAccountRepository> { MyAccountRepositoryImpl(get()) }
}

val remoteDataSourceModule = module {
   factory { MyAccountRemoteDataSource(get(), get()) }
}

val apiModule = module {
    factory("myAccountApi") {
        createCieloService(
            MyAccountApi::class.java,
            BuildConfig.HOST_API,
            get()
        )
    }
}

val myAccountModule = listOf(
    viewModule,
    useCaseModule,
    repositoryModule,
    remoteDataSourceModule,
    apiModule
)