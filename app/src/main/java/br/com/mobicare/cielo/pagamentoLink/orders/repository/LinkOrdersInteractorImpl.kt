package br.com.mobicare.cielo.pagamentoLink.orders.repository

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LinkOrdersInteractorImpl(private val repository: LinkOrdersRepository,
                               private val api: CieloAPIServices) : LinkOrdersInteractor {
    private var compositeDisp = CompositeDisposable()

    /**
     * método para limpar o disposable
     * */
    override fun disposable() {
        compositeDisp.clear()
    }


    override fun getOrders(linkId: String) = repository.getOrders(linkId)
    override fun deleteLink(linkId: String) = repository.deleteLink(linkId)
    override fun isFeatureToggleLoggi() = repository.isFeatureToggleLoggi()
    override fun getOrder(orderId: String): Observable<Order> = repository.getOrder(orderId)

    override fun callMotoboy(orderId: String, callback: APICallbackDefault<ResponseMotoboy, String>) {
        compositeDisp.add(api.callMotoboy(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
        )
    }

    override fun resendCallMotoboy(orderId: String, callback: APICallbackDefault<ResponseMotoboy, String>) {
        compositeDisp.add(api.resendCallMotoboy(orderId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }
}