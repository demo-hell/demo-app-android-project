package br.com.mobicare.cielo.webView.di

import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.webView.analytics.WebViewContainerAnalytics
import br.com.mobicare.cielo.webView.presentation.WebViewContainerViewModel
import br.com.mobicare.cielo.webView.utils.WebViewSharedConfiguration
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { WebViewContainerViewModel(get(), MenuPreference.instance) }
}

val webViewAnalytics = module {
    single (name = "WebViewContainerAnalytics") {
        WebViewContainerAnalytics()
    }
}

val sharedConfigurationModule = module {
    factory { WebViewSharedConfiguration(get()) }
}

val webViewContainerDiModulesList = listOf(viewModelModule, webViewAnalytics, sharedConfigurationModule)