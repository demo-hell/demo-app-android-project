package br.com.mobicare.cielo.openFinance.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.openFinance.data.datasource.ApproveConsentDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.BrandsDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.ChangeOrRenewShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.ConfirmShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.ConsentDetailDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.CreateShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.DetainerDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.EndShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.GivenUpShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.PixMerchantDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.RejectConsentDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.SharedDataConsentsDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.TermsOfUseDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.UpdateShareDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.UserCardBalanceDataSource
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI
import br.com.mobicare.cielo.openFinance.data.repository.ApproveConsentRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.BrandsRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.ChangeOrRenewShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.ConfirmShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.ConsentDetailRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.CreateShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.DetainerRemoteRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.EndShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.GivenUpShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.PixMerchantRemoteRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.RejectConsentRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.SharedDataConsentsRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.TermsOfUseRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.UpdateShareRepositoryImpl
import br.com.mobicare.cielo.openFinance.data.repository.UserCardBalanceRepositoryImpl
import br.com.mobicare.cielo.openFinance.domain.repository.ApproveConsentRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.BrandsRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.ChangeOrRenewShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.ConfirmShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.ConsentDetailRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.CreateShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.DetainerRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.EndShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.GivenUpShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.RejectConsentRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.SharedDataConsentsRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.TermsOfUseRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.UpdateShareRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.UserCardBalanceRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.usecase.ApproveConsentUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.BrandsUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.ChangeOrRenewShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.ConfirmShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.ConsentDetailUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.CreateShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.EndShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetDetainerUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetPixMerchantListOpenFinanceUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetUserCardBalanceUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GivenUpShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.RejectConsentUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.SharedDataConsentsUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.TermsOfUseUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.UpdateShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.home.OpenFinanceHomeViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.OpenFinanceManagerViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.OpenFinanceNewShareViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion.OpenFinanceConclusionViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.redirect.OpenFinanceRedirectViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.OpenFinanceSharedDataViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.consentDetail.ConsentDetailViewModel
import br.com.mobicare.cielo.openFinance.presentation.resume.ResumePaymentHolderViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { OpenFinanceHomeViewModel(get()) }
    viewModel { ResumePaymentHolderViewModel(get(), get(), get(), get(), get()) }
    viewModel { OpenFinanceManagerViewModel(get(), get(), get()) }
    viewModel { OpenFinanceSharedDataViewModel(get(), get()) }
    viewModel { ConsentDetailViewModel(get(), get(), get()) }
    viewModel { OpenFinanceNewShareViewModel(get(), get(), get(), get()) }
    viewModel { OpenFinanceRedirectViewModel() }
    viewModel { OpenFinanceConclusionViewModel(get(), get(), get(), get()) }
}

val useCaseModule = module {
    factory { GetPixMerchantListOpenFinanceUseCase(get()) }
    factory { GetDetainerUseCase(get()) }
    factory { GetUserCardBalanceUseCase(get()) }
    factory { ApproveConsentUseCase(get()) }
    factory { RejectConsentUseCase(get()) }
    factory { SharedDataConsentsUseCase(get()) }
    factory { ConsentDetailUseCase(get()) }
    factory { BrandsUseCase(get()) }
    factory { CreateShareUseCase(get()) }
    factory { UpdateShareUseCase(get()) }
    factory { TermsOfUseUseCase(get()) }
    factory { ConfirmShareUseCase(get()) }
    factory { GivenUpShareUseCase(get()) }
    factory { ChangeOrRenewShareUseCase(get()) }
    factory { EndShareUseCase(get()) }
}

val repositoryModule = module {
    factory<PixMerchantRemoteRepository> { PixMerchantRemoteRepositoryImpl(get()) }
    factory<DetainerRemoteRepository> { DetainerRemoteRepositoryImpl(get()) }
    factory<UserCardBalanceRemoteRepository> { UserCardBalanceRepositoryImpl(get()) }
    factory<ApproveConsentRemoteRepository> { ApproveConsentRepositoryImpl(get()) }
    factory<RejectConsentRemoteRepository> { RejectConsentRepositoryImpl(get()) }
    factory<SharedDataConsentsRemoteRepository> { SharedDataConsentsRepositoryImpl(get()) }
    factory<ConsentDetailRemoteRepository> { ConsentDetailRepositoryImpl(get()) }
    factory <BrandsRemoteRepository>{ BrandsRepositoryImpl(get())  }
    factory <CreateShareRemoteRepository>{ CreateShareRepositoryImpl(get())  }
    factory <UpdateShareRemoteRepository>{ UpdateShareRepositoryImpl(get()) }
    factory <TermsOfUseRemoteRepository>{ TermsOfUseRepositoryImpl(get()) }
    factory <ConfirmShareRemoteRepository>{ ConfirmShareRepositoryImpl(get()) }
    factory <GivenUpShareRemoteRepository>{ GivenUpShareRepositoryImpl(get()) }
    factory <ChangeOrRenewShareRemoteRepository>{ ChangeOrRenewShareRepositoryImpl(get()) }
    factory <EndShareRemoteRepository>{ EndShareRepositoryImpl(get()) }
}

val dataSourceModule = module {
    factory { PixMerchantDataSource(get(), get()) }
    factory { DetainerDataSource(get(), get()) }
    factory { UserCardBalanceDataSource(get(), get()) }
    factory { ApproveConsentDataSource(get(), get()) }
    factory { RejectConsentDataSource(get(), get()) }
    factory { SharedDataConsentsDataSource(get(), get()) }
    factory { ConsentDetailDataSource(get(), get()) }
    factory { BrandsDataSource(get(), get())  }
    factory { CreateShareDataSource(get(), get())  }
    factory { UpdateShareDataSource(get(), get()) }
    factory { TermsOfUseDataSource(get(), get()) }
    factory { ConfirmShareDataSource(get(), get()) }
    factory { GivenUpShareDataSource(get(), get()) }
    factory { ChangeOrRenewShareDataSource(get(), get()) }
    factory { EndShareDataSource(get(), get()) }
}

val apiModule = module {
    factory("openFinanceAPI") {
        createCieloService(HolderAPI::class.java, BuildConfig.HOST_API, get())
    }
}

val holderModuleList = listOf(
    viewModelModule, useCaseModule, repositoryModule, dataSourceModule, apiModule
)