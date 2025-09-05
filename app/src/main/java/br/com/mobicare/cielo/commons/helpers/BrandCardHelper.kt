package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.URL_BRAND_CARD_IMAGES
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference

class BrandCardHelper {

    companion object {
        const val THREE_ZERO = "000"
        const val TWO_ZERO = "00"
        const val ONE_ZERO = "0"

        fun getUrlBrandImageByCode(code: Int) : String? {
            return getUrlBrandImageByCode(code.toString())
        }

        fun getUrlBrandImageByCode(code: String) : String? {
            var url: String? = null
            ConfigurationPreference.instance.getConfigurationValue(URL_BRAND_CARD_IMAGES,
                    BuildConfig.CARD_BRAND_IMAGE_URL)?.let {
                url = it.replace("{0}", code)
            }
            return url
        }

        fun getLoadBrandImageGeneric(code: String, path: String = BuildConfig.LOAD_BRAND_IMAGE_GENERIC) = path.replace("{0}", getVerification(code))

        private fun getVerification(code: String?) = when  {
            (code?.length == 1)-> "${THREE_ZERO}$code"
            (code?.length == 2) -> "${TWO_ZERO}$code"
            (code?.length == 3) -> "${ONE_ZERO}$code"
            else -> "$code"
        }

        fun getUrlBrandImageByCodeConciliator(code: String?): String {
            if (code == null || code.toLong() < 1)
                return ""

            return BuildConfig.CARD_BRAND_IMAGE_URL_CONCILIADOR.replace("{0}", code)
        }

    }
}