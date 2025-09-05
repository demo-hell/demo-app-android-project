package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.LogWrapperUtil
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersInteractor
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.net.HttpURLConnection

class LinkOrderDetailPresenterImpl(private val view: LinkOrderDetailView,
                                   private val interactor: LinkOrdersInteractor) : LinkOrderDetailPresenter {

    private val disposible = CompositeDisposable()
    private var isFeatureToggleLoggi = false
    private lateinit var order: Order

    override fun onCreate(order: Order) {
        this.order = order
        isFeatureToggleLoggi = interactor.isFeatureToggleLoggi()

        this.order.id?.let {
            interactor.getOrder(it)
                .configureIoAndMainThread()
                .doOnSubscribe { view.showLoading() }
                .doFinally { view.hideLoading() }
                .subscribe({
                    this.order = it
                    onOrderSucess(this.order)
                }, {
                    val error = APIUtils.convertToErro(it)
                    when (error.httpStatus) {
                        401 -> Unit
                        else -> verificationStatusError(error)
                    }
                })
                .addTo(disposible)
        }
    }

    override fun getStatusLoggi() {
        view.showLoading()
        order.id?.let {
            interactor.resendCallMotoboy(it, object : APICallbackDefault<ResponseMotoboy, String> {
                override fun onSuccess(response: ResponseMotoboy) {
                    this@LinkOrderDetailPresenterImpl.view.hideLoading()
                    execute(Result.Success(response), true)
                }

                override fun onError(error: ErrorMessage) {
                    this@LinkOrderDetailPresenterImpl.view.hideLoading()
                    verificationStatusError(error)
                }
            })
        }
    }

    private fun onOrderSucess(order: Order) {
        view.initCustomer(order)
        view.initSale()
        view.initSaleStatus()

        val color = order.payment?.status?.let {
            StatusPay
                .valueOf(it).color
        }
        if (color != null) {
            view.setStatusColor(color)
        }

        if (order.shipping?.allowDeliverer == true
                && isFeatureToggleLoggi)
            view.showLoggiButton()
        else if (order.shipping?.allowTracking == true
                && isFeatureToggleLoggi)
            view.showTrackLoggiButton()
    }

    /**
     * método que verifica qual é o status de error vinda da api e redireciona
     * @param error
     * */
    private fun verificationStatusError(error: ErrorMessage) {
        when (error.httpStatus) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> LogWrapperUtil.info("já foi tratado no retrofit")
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error))
            HttpURLConnection.HTTP_NOT_FOUND -> execute(Result.Error.NotFound(error))
            HttpURLConnection.HTTP_INTERNAL_ERROR -> execute(Result.Error.ServerError(error))
            else -> execute(Result.Error.ServerError(error))
        }
    }

    /**
     * método que faz a lógica para onde tem que ser enviada a resposta vinda da api
     * @param result
     * */
    private fun execute(result: Result<Any>, isResendMotoboy: Boolean = false) = when (result) {
        is Result.Success -> view.getStatusLoggiSuccess(result.data as ResponseMotoboy)
        is Result.Error.ServerError -> view.serverError(result.error)
        is Result.Error.Enhance -> view.enhance()
        is Result.Error.NotFound -> view.notFound()
        else -> view.serverError((result as ErrorMessage))
    }
}