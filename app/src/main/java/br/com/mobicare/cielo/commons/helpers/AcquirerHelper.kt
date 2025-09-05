package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.BuildConfig

class AcquirerHelper {

    fun getAcquirerImageByCodeConciliator(code: String?): String {
        if (code == null || code.toLong() < 1) return ""

        return BuildConfig.CARD_ACQUIRER_IMAGE_URL_CONCILIADOR.replace("{0}", code)
    }
}