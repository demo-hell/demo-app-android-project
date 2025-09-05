package br.com.mobicare.cielo.newRecebaRapido.analytics

import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase

object RAAF {
    fun logRAHiredScreenView(transactionType: String) {
        AppsFlyerUtil.send(
            context = CieloApplication.context,
            event = Events.RA_HIRED,
            map =
                mapOf(
                    AppsFlyerConstants.AF_SCREEN_NAME to
                        ScreenView.RA_HIRING_SALE_TYPE_SUCCESS.format(
                            transactionType.normalizeToLowerSnakeCase(),
                        ),
                    AppsFlyerConstants.AF_PRODUCT to Values.AUTOMATIC_RECEIVING,
                ),
        )
    }

    object Events {
        const val RA_HIRED = "ra_contratou"
    }

    object Values {
        const val AUTOMATIC_RECEIVING = "recebimento_automatico"
    }

    object ScreenView {
        const val RA_HIRING_SALE_TYPE_SUCCESS = "/recebimento_automatico/contratacao/%s/sucesso"
    }
}
