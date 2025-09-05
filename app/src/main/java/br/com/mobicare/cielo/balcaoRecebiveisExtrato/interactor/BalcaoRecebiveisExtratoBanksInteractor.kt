package br.com.mobicare.cielo.balcaoRecebiveisExtrato.interactor

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoBanksContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread

/**
 * create by Enzo Teles
 * Jan 19, 2021
 * */
class BalcaoRecebiveisExtratoBanksInteractor(private val api: CieloAPIServices) : BalcaoRecebiveisExtratoBanksContract.Interactor{

    private var compositeDisp = CompositeDisposableHandler()
    override fun getBanks(
        initDate: String,
        finalDate: String,
        type: String,
        callback: APICallbackDefault<NegotiationsBanks, String>
    ) {
        compositeDisp.compositeDisposable.add(api.loadNegotiationsBanks(initDate, finalDate, type)
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    /**
     * method to clear disposable
     * */
    override fun cleanDisposable() {
        compositeDisp.destroy()
    }

    /**
     * method to resume disposable
     * */
    override fun resumeDisposable() {
        compositeDisp.start()
    }
}