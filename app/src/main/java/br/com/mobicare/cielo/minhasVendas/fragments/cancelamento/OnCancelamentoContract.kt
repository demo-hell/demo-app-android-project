package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mySales.data.model.Sale


interface OnCancelamentoContract{
    interface View {
        fun onSucess(response: ResponseBanlanceInquiry){}
        fun onError(error: ErrorMessage){}
        fun onSucessVendaCancelada(response: ResponseCancelVenda){}
    }

    interface Presenter {
        fun balanceInquiry(item: Sale)
        fun sendVendaToCancel(
            sale: ArrayList<RequestCancelApi>,
            currentOtpGenerated: String
        )
    }

    interface Interactor {
        fun balanceInquiry(item: Sale, apiCallbackDefault: APICallbackDefault<ResponseBanlanceInquiry, String>)
        fun disposable()
        fun sendVendaToCancel(
            sale: ArrayList<RequestCancelApi>,
            currentOtpGenerated: String,
            apiCallbackDefault: APICallbackDefault<ResponseCancelVenda, String>
        )
    }
}
