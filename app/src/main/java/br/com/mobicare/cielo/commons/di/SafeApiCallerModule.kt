package br.com.mobicare.cielo.commons.di

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloInterceptor
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

val safeApiCallerModule = module {
    single { SafeApiCaller(Dispatchers.IO) }
}
val cieloInterceptorModule = module {
    factory {
        CieloInterceptor(
            cieloMfaTokenGenerator = get(),
            menuPreferences = MenuPreference.instance,
            mfaUserInformation = MfaUserInformation(get())
        )
    }
}
val safeApiCallerModulesList = listOf(
    safeApiCallerModule, cieloInterceptorModule
)