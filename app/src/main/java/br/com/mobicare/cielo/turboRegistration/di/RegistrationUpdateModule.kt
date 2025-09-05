package br.com.mobicare.cielo.turboRegistration.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.data.dataSource.AddressDataSource
import br.com.mobicare.cielo.turboRegistration.data.dataSource.BankInfoDataSource
import br.com.mobicare.cielo.turboRegistration.data.dataSource.BusinessSectorDataSource
import br.com.mobicare.cielo.turboRegistration.data.dataSource.EligibilityDataSource
import br.com.mobicare.cielo.turboRegistration.data.dataSource.MonthlyIncomeDataSource
import br.com.mobicare.cielo.turboRegistration.data.dataSource.remote.RegistrationServerApi
import br.com.mobicare.cielo.turboRegistration.data.repository.AddressRepositoryImpl
import br.com.mobicare.cielo.turboRegistration.data.repository.BankInfoRepositoryImpl
import br.com.mobicare.cielo.turboRegistration.data.repository.BusinessSectorRepositoryImpl
import br.com.mobicare.cielo.turboRegistration.data.repository.EligibilityRepositoryImpl
import br.com.mobicare.cielo.turboRegistration.data.repository.MonthlyIncomeRepositoryImpl
import br.com.mobicare.cielo.turboRegistration.domain.repository.AddressRepository
import br.com.mobicare.cielo.turboRegistration.domain.repository.BankInfoRepository
import br.com.mobicare.cielo.turboRegistration.domain.repository.BusinessSectorRepository
import br.com.mobicare.cielo.turboRegistration.domain.repository.EligibilityRepository
import br.com.mobicare.cielo.turboRegistration.domain.repository.MonthlyIncomeRepository
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetAddressByCepUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetEligibilityUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetOperationsUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.RegisterNewAccountUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.SearchBanksUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.SearchBusinessLinesUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateAddressUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateBusinessSectorUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateMonthlyIncomeUseCase
import br.com.mobicare.cielo.turboRegistration.presentation.TurboRegistrationViewModel
import br.com.mobicare.cielo.turboRegistration.presentation.bankData.BankInfoViewModel
import br.com.mobicare.cielo.turboRegistration.presentation.businessSector.BusinessSectorViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val turboRegistrationViewModel = module {
    viewModel { TurboRegistrationViewModel(get(), get()) }
    viewModel { BankInfoViewModel(get(), get()) }
    viewModel { BusinessSectorViewModel(get()) }
    viewModel { RegistrationUpdateViewModel(get(), get(), get(), get(), get()) }
}

val registrationRepository = module {
    factory<EligibilityRepository> { EligibilityRepositoryImpl(get()) }
    factory<BankInfoRepository> { BankInfoRepositoryImpl(get()) }
    factory<BusinessSectorRepository> { BusinessSectorRepositoryImpl(get()) }
    factory<AddressRepository> { AddressRepositoryImpl(get()) }
    factory<MonthlyIncomeRepository> { MonthlyIncomeRepositoryImpl(get()) }
}

val registrationApi = module {
    factory("registrationApi"){
        createCieloService(RegistrationServerApi::class.java, BuildConfig.HOST_API, get())
    }
}

val registrationDataSourceModule = module {
    factory { EligibilityDataSource(get(), get()) }
    factory { BankInfoDataSource(get(), get()) }
    factory { BusinessSectorDataSource(get(), get()) }
    factory { AddressDataSource(get(), get()) }
    factory { MonthlyIncomeDataSource(get(), get()) }
}

val registrationUseCaseModule = module {
    factory { GetEligibilityUseCase(get()) }
    factory { SearchBanksUseCase(get()) }
    factory { SearchBusinessLinesUseCase(get()) }
    factory { GetOperationsUseCase(get()) }
    factory { GetAddressByCepUseCase(get()) }
    factory { RegisterNewAccountUseCase(get()) }
    factory { UpdateAddressUseCase(get()) }
    factory { UpdateBusinessSectorUseCase(get()) }
    factory { UpdateMonthlyIncomeUseCase(get()) }
}

val registrationUpdateModulesList = listOf(turboRegistrationViewModel, registrationUseCaseModule, registrationRepository, registrationDataSourceModule, registrationApi)