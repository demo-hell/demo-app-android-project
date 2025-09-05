package br.com.mobicare.cielo.arv.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.data.datasource.ArvRemoteDataSource
import br.com.mobicare.cielo.arv.data.datasource.remote.ArvServerApi
import br.com.mobicare.cielo.arv.data.repository.ArvRepositoryNewImpl
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.arv.domain.useCase.*
import br.com.mobicare.cielo.arv.domain.useCase.GetArvBanksUseCase
import br.com.mobicare.cielo.arv.presentation.ArvEffectiveTimeViewModel
import br.com.mobicare.cielo.arv.presentation.anticipation.*
import br.com.mobicare.cielo.arv.presentation.historic.list.ArvHistoricListViewModel
import br.com.mobicare.cielo.arv.presentation.home.ArvHomeViewModel
import br.com.mobicare.cielo.arv.presentation.home.whatsAppNews.ArvWhatsAppNewsViewModel
import br.com.mobicare.cielo.arv.presentation.onboarding.ArvOnboardingViewModel
import br.com.mobicare.cielo.arv.presentation.router.ArvRouterViewModel
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.home.presentation.arv.viewmodel.ArvCardAlertHomeViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val arvViewModelModule =
    module {
        viewModel { ArvOnboardingViewModel(get()) }
        viewModel { ArvRouterViewModel(get(), get()) }
        viewModel { ArvHomeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { ArvSingleAnticipationViewModel(get(), get(), get()) }
        viewModel { ArvSimulationViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { ArvHistoricListViewModel(get(), get()) }
        viewModel { ArvFilterSelectionViewModel(get(), get()) }
        viewModel {
                (args: ArvScheduledAnticipationBankSelectFragmentArgs) ->
            ArvScheduledAnticipationBankSelectViewModel(args, get(), get(), get())
        }
        viewModel { ArvScheduledConfirmationViewModel(get(), get(), get(), get(), get()) }
        viewModel { ArvSingleAnticipationSimulateWithValueViewModel(get(), get()) }
        viewModel { ArvEffectiveTimeViewModel(get()) }
        viewModel { ArvCancelScheduledViewModel(get(), get()) }
        viewModel { ArvCardAlertHomeViewModel(get(), get(), get()) }
        viewModel { ArvWhatsAppNewsViewModel(get(), get(), get(), get(), get()) }
    }

val arvRemoteDataSourceModule =
    module {
        factory { ArvRemoteDataSource(get(), get()) }
    }

val arvRepositoryModule =
    module {
        factory<ArvRepositoryNew> { ArvRepositoryNewImpl(get()) }
    }

val arvUseCaseModule =
    module {
        factory { GetArvAnticipationNewUseCase(get()) }
        factory { GetArvAnticipationHistoryNewUseCase(get()) }
        factory { GetArvSingleAnticipationWithDateNewUseCase(get()) }
        factory { GetArvBanksUseCase(get()) }
        factory { ConfirmArvAnticipationUseCase(get()) }
        factory { GetArvSingleAnticipationWithFilterUseCase(get()) }
        factory { GetArvScheduledAnticipationUseCase(get()) }
        factory { ConfirmArvScheduledAnticipationUseCase(get()) }
        factory { GetArvSingleAnticipationWithValueNewUseCase(get()) }
        factory { GetArvOptInUseCase(get()) }
        factory { GetArvScheduledContractUseCase(get()) }
        factory { GetArvBranchContractsUseCase(get()) }
        factory { CancelArvScheduledAnticipationUseCase(get()) }
    }

val arvApiModule =
    module {
        factory("arvServerApi") {
            createCieloService(
                ArvServerApi::class.java,
                BuildConfig.HOST_API,
                get(),
            )
        }
    }

val arvAnalytics =
    module {
        factory(name = "ArvAnalytics") {
            ArvAnalytics()
        }
    }

val arvAnalyticsGA4 =
    module {
        single(name = "ArvAnalyticsGA4") {
            ArvAnalyticsGA4()
        }
    }

val arvModulesList =
    listOf(
        arvViewModelModule,
        arvUseCaseModule,
        arvRemoteDataSourceModule,
        arvRepositoryModule,
        arvApiModule,
        arvAnalytics,
        arvAnalyticsGA4,
    )
