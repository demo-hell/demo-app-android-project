package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mySales.data.model.Sale

class CancelamentoPresenter(val mView: OnCancelamentoContract.View, val interactor: OnCancelamentoContract.Interactor) : OnCancelamentoContract.Presenter, LifecycleObserver {


    /**
     * método que consulta o saldo da venda
     * @param item
     * */
    override fun balanceInquiry(item: Sale) {
        interactor.balanceInquiry(item, object : APICallbackDefault<ResponseBanlanceInquiry, String> {
            override fun onSuccess(response: ResponseBanlanceInquiry) {
                mView.onSucess(response)
            }

            override fun onError(error: ErrorMessage) {
                mView.onError(error)
            }
        })
    }

    override fun sendVendaToCancel(
        sales: ArrayList<RequestCancelApi>,
        currentOtpGenerated: String
    ) {

        interactor.sendVendaToCancel(sales, currentOtpGenerated, object : APICallbackDefault<ResponseCancelVenda, String> {
            override fun onSuccess(response: ResponseCancelVenda) {
                mView.onSucessVendaCancelada(response)
            }

            override fun onError(error: ErrorMessage) {
                mView.onError(error)
            }
        })
    }

    /**
     * método para limpar o disposible
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        interactor.disposable()
    }

}