package br.com.mobicare.cielo.pagamentoLink.orders

import br.com.mobicare.cielo.pagamentoLink.orders.model.Order

interface LinkOrdersView {

    fun onOrdersSuccess(orders: List<Order>)
    fun onOrderError()
    fun onDeleteLinkError()
    fun onOrderListEmpty()
    fun onDeleteLinkSuccess()
    fun showLoading()
    fun hideLoading()
    fun copyLink()
}