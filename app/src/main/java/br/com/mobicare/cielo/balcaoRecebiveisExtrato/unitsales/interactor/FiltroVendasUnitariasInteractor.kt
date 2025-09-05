package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.interactor

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoBanksContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.FiltroVendasUnitariasContract
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import io.reactivex.disposables.CompositeDisposable


class FiltroVendasUnitariasInteractor(private val api: CieloAPIServices) : FiltroVendasUnitariasContract.Interactor{

    private var compositeDisp = CompositeDisposable()

    override fun getBrands(
        date:String,
        identificationNumber:String,
        callback: APICallbackDefault<VendasUnitariasFilterBrands, String>
    ) {
        compositeDisp.add(api.loadFiltroVendasUnitariasBrands(date, identificationNumber)
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    override fun cleanDisposable() {
        compositeDisp.clear()
    }
}