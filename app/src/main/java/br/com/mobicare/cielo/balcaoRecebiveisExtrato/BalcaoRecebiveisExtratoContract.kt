package br.com.mobicare.cielo.balcaoRecebiveisExtrato

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.Observable

interface BalcaoRecebiveisExtratoContract {
    interface View {
        fun initView()
        fun initProgress()
        fun finishedProgress()
        fun showProgressMore() {}
        fun finishedProgressMore() {}
        fun showSuccess(negotiations: Negotiations)
        fun serverError()
        fun logScreenView(screenName: String) {}
        fun logException(screenName: String, error: NewErrorMessage) {}
    }

    interface Interactor {
        fun getNegotiations(
            initDate: String,
            finalDate: String,
            apiCallbackDefault: APICallbackDefault<Negotiations, String>
        )

        fun getNegotiationsType(
            page: Int,
            pageSize: Int,
            initDate: String,
            finalDate: String,
            type: String,
            quickFilter: QuickFilter?,
            apiCallbackDefault: APICallbackDefault<Negotiations, String>
        )

        fun cleanDisposable()
        fun resumeDisposable()
        fun getUnitReceivable(
            page: Int,
            pageSize: Int,
            negotiationDate: String,
            operationNumber: String,
            initialReceivableDate: String?,
            finalReceivableDate: String?,
            identificationNumber: String?,
            options: ArrayList<Int>
        ): Observable<ExtratoRecebiveisVendasUnitariasResponse>
    }
}