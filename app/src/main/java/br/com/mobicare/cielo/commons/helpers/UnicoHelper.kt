package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.constants.MainConstants.FLAVOR_STORE
import com.acesso.acessobio_android.onboarding.AcessoBioConfigDataSource

fun getUnicoJson(): String {
    return if (BuildConfig.FLAVOR.contains(FLAVOR_STORE, true))
        "unico-check-mobile-services-p.json"
    else
        "unico-check-mobile-services.json"
}

fun getUnicoConfig(): AcessoBioConfigDataSource {
    return object : AcessoBioConfigDataSource {
        override fun getBundleIdentifier(): String {
            return BuildConfig.APPLICATION_ID
        }

        override fun getHostInfo(): String {
            return BuildConfig.UNICO_HOST_INFO
        }

        override fun getHostKey(): String {
            return BuildConfig.UNICO_HOST_KEY
        }

        override fun getMobileSdkAppId(): String {
            return BuildConfig.UNICO_MOBILE_SDK_APP_ID
        }

        override fun getProjectId(): String {
            return BuildConfig.UNICO_PROJECT_ID
        }

        override fun getProjectNumber(): String {
            return BuildConfig.UNICO_PROJECT_NUMBER
        }
    }
}