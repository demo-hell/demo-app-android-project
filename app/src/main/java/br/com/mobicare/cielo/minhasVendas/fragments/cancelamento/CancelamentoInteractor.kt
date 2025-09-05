package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.mySales.data.model.Sale
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CancelamentoInteractor(val api: CieloAPIServices) : OnCancelamentoContract.Interactor {

    private var compositeDisp = CompositeDisposable()

    /**
     * método para limpar o disposable
     * */
    override fun disposable() {
        compositeDisp.clear()
    }

    /**
     * método para consultar o saldo da venda
     * @param item callback
     * */
    override fun balanceInquiry(item: Sale, callback: APICallbackDefault<ResponseBanlanceInquiry, String>) {
        val token = UserPreferences.getInstance().token

        token?.let {
            compositeDisp.add(api.balanceInquiry(item, it, item.authorizationDate.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        callback.onSuccess(it)
                    }, {
                        val errorMessage = APIUtils.convertToErro(it)
                        callback.onError(errorMessage)
                    }))

        }

    }

    override fun sendVendaToCancel(
        sales: ArrayList<RequestCancelApi>,
        currentOtpGenerated: String,
        callback: APICallbackDefault<ResponseCancelVenda, String>
    ) {

        val token = UserPreferences.getInstance().token
        token?.let {
            compositeDisp.add(api.sendSaleToCancel(sales, currentOtpGenerated, it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        callback.onSuccess(it)
                    }, {
                        val errorMessage = APIUtils.convertToErro(it)
                        callback.onError(errorMessage)
                    }))
        }

    }

}