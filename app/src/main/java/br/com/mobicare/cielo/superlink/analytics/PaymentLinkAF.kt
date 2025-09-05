package br.com.mobicare.cielo.superlink.analytics

import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil

object PaymentLinkAF {
    fun logAccreditationScreenView() {
        AppsFlyerUtil.send(
            context = CieloApplication.context,
            event = Events.PAYMENT_LINK_START,
            obj = Pair(AppsFlyerConstants.AF_SCREEN_NAME, ScreenView.PAYMENT_LINK_HIRE),
        )
    }

    fun logLinkCreatedScreenView() {
        AppsFlyerUtil.send(
            context = CieloApplication.context,
            event = Events.PAYMENT_LINK_SUCCESS,
            obj = Pair(AppsFlyerConstants.AF_SCREEN_NAME, ScreenView.PAYMENT_LINK_SUCCESS),
        )
    }

    object Events {
        const val PAYMENT_LINK_START = "link-de-pagamento_inicio"
        const val PAYMENT_LINK_SUCCESS = "link-de-pagamento_sucesso"
    }

    object ScreenView {
        const val PAYMENT_LINK_HIRE = "/link_de_pagamento/contratar"
        const val PAYMENT_LINK_SUCCESS = "/link_de_pagamento/sucesso"
    }
}
