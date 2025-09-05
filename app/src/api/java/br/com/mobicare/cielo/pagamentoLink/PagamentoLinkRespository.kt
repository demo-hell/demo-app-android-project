package br.com.mobicare.cielo.pagamentoLink

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLinkResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PagamentoLinkRespository(private val remoteDataSource: PagamentoLinkDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun paymentLinkActivity(token: String, size: Int, page: Int,
                            callback: APICallbackDefault<PaymentLinkResponse, String>) {
        compositeDisp.add(remoteDataSource.paymentLinkActivity(token, size, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))

    }

}