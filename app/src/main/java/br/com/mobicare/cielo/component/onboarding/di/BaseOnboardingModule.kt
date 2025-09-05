package br.com.mobicare.cielo.component.onboarding.di

import br.com.mobicare.cielo.component.onboarding.viewModel.BaseOnboardingViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { BaseOnboardingViewModel(get()) }
}

val baseOnboardingModuleList = listOf(viewModelModule)