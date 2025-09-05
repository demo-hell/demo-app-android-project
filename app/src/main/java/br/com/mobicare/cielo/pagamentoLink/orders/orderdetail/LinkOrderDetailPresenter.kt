package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail

import br.com.mobicare.cielo.pagamentoLink.orders.model.Order

interface LinkOrderDetailPresenter {

    fun onCreate(order: Order)
    fun getStatusLoggi()
}