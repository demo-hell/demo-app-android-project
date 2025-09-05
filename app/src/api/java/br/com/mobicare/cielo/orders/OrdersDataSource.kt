package br.com.mobicare.cielo.orders

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.orders.domain.OrderReplacementRequest
import br.com.mobicare.cielo.orders.domain.OrderRequest

class OrdersDataSource(context: Context) {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun loadOrdersAvailability(token: String)
            = api.loadOrdersAvailability(token)

    fun postOrders(token: String, orderRequest: OrderRequest)
            = this.api.postOrders(token, orderRequest)

    fun postOrdersReplacements(token: String, orderRequest: OrderReplacementRequest)
            = this.api.postOrdersReplacements(token, orderRequest)

}