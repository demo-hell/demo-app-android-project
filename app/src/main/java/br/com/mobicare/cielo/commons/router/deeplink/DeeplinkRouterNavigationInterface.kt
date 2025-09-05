package br.com.mobicare.cielo.commons.router.deeplink

import android.content.Context
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel

interface DeeplinkRouterNavigationInterface {
    fun startNavigation(context: Context, deepLinkModel: DeepLinkModel)
}