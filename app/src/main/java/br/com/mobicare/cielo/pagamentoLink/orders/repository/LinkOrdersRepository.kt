package br.com.mobicare.cielo.pagamentoLink.orders.repository

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink

class LinkOrdersRepository(private val api: CieloAPIServices) {

    private val token = UserPreferences.getInstance().token

    fun getOrders(linkId: String) = api.getLinkOrders(token, linkId)
    fun getOrder(orderId: String) = api.getLinkOrder(token, orderId)
    fun deleteLink(linkId: String) = api.deleleLink(token, DeleteLink(linkId))

    fun isFeatureToggleLoggi() = FeatureTogglePreference
            .instance
            .getFeatureTogle(FeatureTogglePreference.SUPERLINK_ENTREGA_LOGGI)
}