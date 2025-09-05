package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.Observable


interface FiltroVendasUnitariasContract {
    interface View {
        fun initView()
        fun initProgress()
        fun finishedProgress()
        fun showSuccess(brands: VendasUnitariasFilterBrands)
        fun serverError()
    }

    interface Interactor {
        fun getBrands(initDate:String, finalDate:String, apiCallbackDefault: APICallbackDefault<VendasUnitariasFilterBrands, String>)
        fun cleanDisposable()
    }
}