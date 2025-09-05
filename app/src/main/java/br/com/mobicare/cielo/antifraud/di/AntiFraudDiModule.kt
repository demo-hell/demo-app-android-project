package br.com.mobicare.cielo.antifraud.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.antifraud.ThreatMetrixProfilerImpl
import br.com.mobicare.cielo.antifraud.data.dataSource.AntiFraudDataSource
import br.com.mobicare.cielo.antifraud.data.dataSource.remote.AntiFraudAPI
import br.com.mobicare.cielo.antifraud.data.repository.AntiFraudRepositoryImpl
import br.com.mobicare.cielo.antifraud.domain.repository.AntiFraudRepository
import br.com.mobicare.cielo.antifraud.domain.useCase.GetAntiFraudSessionIDUseCase
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val useCaseModule = module {
    factory { GetAntiFraudSessionIDUseCase(get(), ThreatMetrixProfilerImpl(androidContext())) }
}

val repositoryModule = module {
    factory<AntiFraudRepository> {
        AntiFraudRepositoryImpl(get())
    }
}

val dataSourceModule = module {
    factory { AntiFraudDataSource(get(), get()) }
}

val antiFraudAPI = module {
    factory("antiFraudAPI") {
        createCieloService(AntiFraudAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val antiFraudModuleList = listOf(
    useCaseModule, repositoryModule, dataSourceModule, antiFraudAPI
)