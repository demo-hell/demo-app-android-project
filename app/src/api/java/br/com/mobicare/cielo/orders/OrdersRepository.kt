package br.com.mobicare.cielo.orders

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.machine.domain.OrdersAvailabilityResponse
import br.com.mobicare.cielo.orders.domain.OrderReplacementRequest
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.orders.domain.OrderRequest
import br.com.mobicare.cielo.orders.domain.OrdersResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrdersRepository(private val remoteDataSource: OrdersDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun loadOrdersAvailability(token: String, callback: APICallbackDefault<OrdersAvailabilityResponse, String>) {
        compositeDisp.add(remoteDataSource.loadOrdersAvailability(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
        )
    }

    fun postOrders(token: String, orderRequest: OrderRequest, callback: APICallbackDefault<OrdersResponse, String>) {
        compositeDisp.add(remoteDataSource.postOrders(token, orderRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
        )
    }

    fun postOrdersReplacements(token: String, orderRequest: OrderReplacementRequest, callback: APICallbackDefault<OrderReplacementResponse, String>) {
        compositeDisp.add(remoteDataSource.postOrdersReplacements(token, orderRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
        )
    }


}