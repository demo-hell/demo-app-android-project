package br.com.mobicare.cielo.pagamentoLink.orders

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class LinkOrdersPresenterImpl(
    private val view: LinkOrdersView,
    private val interactor: LinkOrdersInteractor
) : LinkOrdersPresenter {

    private val disposible = CompositeDisposable()
    private var linkId = ""

    override fun onCreate(linkId: String?) {
        linkId?.let {
            this.linkId = linkId
            getOrders()
        }
    }

    /**
     * método para pegar os link da api
     * */
    override fun getOrders() {
        interactor.getOrders(linkId)
            .configureIoAndMainThread()
            .doOnSubscribe { view.showLoading() }
            .doFinally { view.hideLoading() }
            .subscribe({
                if (it.items.isNullOrEmpty())
                    view.onOrderListEmpty()
                else view.onOrdersSuccess(it.items)
            }, {
                val error = APIUtils.convertToErro(it)
                when (error.httpStatus) {
                    401 -> {
                    }
                    else -> view.onOrderError()
                }
            })
            .addTo(disposible)
    }

    /**
     * método para deletar um link na api
     * */
    override fun deleteLink() {
        disposible.add(interactor.deleteLink(linkId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { view.showLoading() }
            .doFinally { view.hideLoading() }
            .subscribe({
                view.onDeleteLinkSuccess()
            }, {
                view.onDeleteLinkError()
            })
        )
    }

    /**
     * método para limpar o dispose
     * */
    override fun onDestroy() {
        disposible.dispose()
    }
}