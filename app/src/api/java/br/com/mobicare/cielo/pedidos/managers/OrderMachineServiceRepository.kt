package br.com.mobicare.cielo.pedidos.managers

import br.com.mobicare.cielo.pedidos.api.OrderMachineDataSource
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import io.reactivex.Observable

class OrderMachineServiceRepository(
    val orderMachineServiceDataSource: OrderMachineDataSource
) : OrderMachineRepository {

    override fun fetchMachineOpenedOrders(page: Int): Observable<OrderMachineResponse> {
        return orderMachineServiceDataSource.fetchOpenedOrders(page)
    }

}