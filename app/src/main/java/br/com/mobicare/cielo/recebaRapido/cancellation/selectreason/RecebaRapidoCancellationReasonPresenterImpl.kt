package br.com.mobicare.cielo.recebaRapido.cancellation.selectreason

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.recebaRapido.cancellation.requested.RecebaRapidoCancellationReasonPresenter

class RecebaRapidoCancellationReasonPresenterImpl(
        private val view: RecebaRapidoCancellationReasonView,
        private val repository: RecebaRapidoRepository) : RecebaRapidoCancellationReasonPresenter {

    private val disposableHandler = CompositeDisposableHandler()

    override fun callDeleteRecebaRapido() {
        this.disposableHandler.compositeDisposable.add(
                this.repository.callDeleteRecebaRapido()
                        .configureIoAndMainThread()
                        .doOnSubscribe { view.showLoading() }
                        .subscribe({
                            if (it.code() in 200..204)
                                view.onCancellationSuccess()
                            else{
                                val error = APIUtils.convertToErro(it)
                                view.onCacelltionError(error)
                            }
                        }, {
                            val error = APIUtils.convertToErro(it)
                            view.onCacelltionError(error)
                        })
        )
    }
}