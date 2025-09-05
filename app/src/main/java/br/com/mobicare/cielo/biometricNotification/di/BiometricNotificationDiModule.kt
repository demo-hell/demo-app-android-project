package br.com.mobicare.cielo.biometricNotification.di

import br.com.mobicare.cielo.biometricNotification.ui.BiometricNotificationBottomSheetViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { BiometricNotificationBottomSheetViewModel(get()) }
}

val biometricNotificationList = listOf(
    viewModelModule
)