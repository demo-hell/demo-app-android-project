package br.com.mobicare.cielo.pagamentoLink.orders

interface LinkOrdersPresenter {

    fun onCreate(linkId: String?)
    fun getOrders()
    fun deleteLink()
    fun onDestroy()
}