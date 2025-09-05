package br.com.mobicare.cielo.biometricToken.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4
import br.com.mobicare.cielo.biometricToken.data.dataSource.BiometricTokenDataSource
import br.com.mobicare.cielo.biometricToken.data.dataSource.remote.BiometricTokenAPI
import br.com.mobicare.cielo.biometricToken.data.repository.BiometricTokenRepositoryImpl
import br.com.mobicare.cielo.biometricToken.domain.BiometricTokenRepository
import br.com.mobicare.cielo.biometricToken.presentation.home.BiometricTokenHomeContract
import br.com.mobicare.cielo.biometricToken.presentation.home.BiometricTokenHomePresenter
import br.com.mobicare.cielo.biometricToken.presentation.password.BiometricTokenPasswordContract
import br.com.mobicare.cielo.biometricToken.presentation.password.BiometricTokenPasswordPresenter
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieContract
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfiePresenter
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val presenterModule = module {
    factory(name = "biometricTokenHomePresenter") { (view: BiometricTokenHomeContract.View) ->
        BiometricTokenHomePresenter(view)
    }

    factory(name = "biometricTokenSelfiePresenter") { (view: BiometricTokenSelfieContract.View) ->
        BiometricTokenSelfiePresenter(
            view,
            get(),
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "biometricTokenPasswordPresenter") { (view: BiometricTokenPasswordContract.View) ->
        BiometricTokenPasswordPresenter(
            view,
            get(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val repositoryModule = module {
    factory<BiometricTokenRepository> { BiometricTokenRepositoryImpl(get()) }
}

val remoteDataSourceModule = module {
    factory { BiometricTokenDataSource(get()) }
}

val apiModule = module {
    factory("BiometricTokenAPI") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(BiometricTokenAPI::class.java) as BiometricTokenAPI
    }
}

val analyticsGA4 = module{
    factory(name = "biometricTokenGA4"){
        BiometricTokenGA4()
    }
}

val biometricTokenModulesList = listOf(
    presenterModule,
    repositoryModule,
    remoteDataSourceModule,
    apiModule,
    analyticsGA4
)