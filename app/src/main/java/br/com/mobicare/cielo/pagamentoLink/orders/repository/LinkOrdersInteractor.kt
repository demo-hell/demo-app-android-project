package br.com.mobicare.cielo.pagamentoLink.orders.repository

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy
import io.reactivex.Completable
import io.reactivex.Observable

interface LinkOrdersInteractor {

    fun getOrders(linkId: String): Observable<LinkOrdersResponse>
    fun deleteLink(linkId: String) : Completable
    fun getOrder(orderId: String): Observable<Order>
    fun isFeatureToggleLoggi() : Boolean
    fun disposable()
    fun callMotoboy(orderId: String, apiCallbackDefault: APICallbackDefault<ResponseMotoboy, String>)
    fun resendCallMotoboy(orderId: String, apiCallbackDefault: APICallbackDefault<ResponseMotoboy, String>)

}