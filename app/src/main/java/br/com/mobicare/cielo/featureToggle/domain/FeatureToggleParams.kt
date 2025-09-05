package br.com.mobicare.cielo.featureToggle.domain

import br.com.mobicare.cielo.BuildConfig

class FeatureToggleParams {
    var system: String? = null
    var version: String? = null
    var platform: String? = null

    companion object {
        private const val SYSTEM = "cielo-app"
        private const val PLATFORM = "android"

        fun getParams(): FeatureToggleParams {
            val params = FeatureToggleParams()
            params.system = SYSTEM
            params.version = BuildConfig.VERSION_NAME
            params.platform = PLATFORM
            return params
        }
    }
}