package br.com.mobicare.cielo.pedidos.api

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import io.reactivex.Observable

class OrderMachineDataSource(private val cieloApi: CieloAPIServices) {

    fun fetchOpenedOrders(page: Int): Observable<OrderMachineResponse> {
        return cieloApi.fetchMachineOpenedOrders(page)
    }

}