package br.com.mobicare.cielo.posVirtual.analytics

import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil

object PosVirtualAF {
    fun logHomeScreenView() {
        AppsFlyerUtil.send(
            context = CieloApplication.context,
            event = Events.POS_VIRTUAL_START,
            obj = Pair(AppsFlyerConstants.AF_SCREEN_NAME, ScreenView.OTHER_POS_VIRTUAL),
        )
    }

    fun logAccreditationInProgressScreenView() {
        AppsFlyerUtil.send(
            context = CieloApplication.context,
            event = Events.POS_VIRTUAL_SUCCESS,
            obj = Pair(AppsFlyerConstants.AF_SCREEN_NAME, ScreenView.OTHER_POS_VIRTUAL_SUCCESS),
        )
    }

    object Events {
        const val POS_VIRTUAL_START = "pos-virtual_inicio"
        const val POS_VIRTUAL_SUCCESS = "pos-virtual_sucesso"
    }

    object ScreenView {
        const val OTHER_POS_VIRTUAL = "/outros/pos_virtual"
        const val OTHER_POS_VIRTUAL_SUCCESS = "/outros/pos_virtual/sucesso"
    }
}
