package br.com.mobicare.cielo.balcaoRecebiveisExtrato.interactor

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

/**
 * create by Enzo Teles
 * Jan 19, 2021
 * */
class BalcaoRecebiveisExtratoInteractor(private val api: CieloAPIServices) : BalcaoRecebiveisExtratoContract.Interactor{

    private var compositeDisp = CompositeDisposableHandler()

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

    override fun getUnitReceivable(
        page: Int, pageSize: Int,
        negotiationDate: String,
        operationNumber: String,
        initialReceivableDate: String?,
        finalReceivableDate: String?,
        identificationNumber: String?,
        options: ArrayList<Int>
    ) = api.getUnitReceivable(
            page,
            pageSize,
            negotiationDate,
            operationNumber,
            initialReceivableDate,
            finalReceivableDate,
            identificationNumber,
            options)


    /**
     * method to load all negotiations of the api
     * @param callback
     * */
    override fun getNegotiations(initDate:String, finalDate:String, callback: APICallbackDefault<Negotiations, String>) {

        compositeDisp.compositeDisposable.add(api.loadNegotiations(initDate, finalDate)
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    override fun getNegotiationsType(
            page: Int,
            pageSize: Int,
        initDate: String,
        finalDate: String,
        type: String,
        quickFilter: QuickFilter?,
        callback: APICallbackDefault<Negotiations, String>
    ) {

        compositeDisp.compositeDisposable.add(api.loadNegotiationsByType(page, pageSize, initDate, finalDate, type, quickFilter)
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

}