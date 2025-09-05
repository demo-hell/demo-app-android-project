package br.com.mobicare.cielo.pedidos.managers

import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import io.reactivex.Observable

interface OrderMachineRepository {

    fun fetchMachineOpenedOrders(page: Int): Observable<OrderMachineResponse>

}