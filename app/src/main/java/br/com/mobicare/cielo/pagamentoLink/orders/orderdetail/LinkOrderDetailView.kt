package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy

interface LinkOrderDetailView {

    fun initCustomer(order: Order)
    fun initSale()
    fun initSaleStatus()
    fun showLoggiButton()
    fun showTrackLoggiButton()
    fun setStatusColor(color: Int)
    fun getStatusLoggiSuccess(responseMotoboy: ResponseMotoboy)
    fun showLoading()
    fun hideLoading()
    fun serverError(error: ErrorMessage)
    fun enhance()
    fun notFound()
}