package br.com.mobicare.cielo.balcaoRecebiveisExtrato

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault


interface BalcaoRecebiveisExtratoBanksContract {
    interface View {
        fun initProgressBanks()
        fun finishedProgressBanks()
        fun showSuccessBanks(banks: NegotiationsBanks)
        fun serverErrorBanks()
        fun logScreenView(screenName: String)
        fun logException(screenName: String, error: NewErrorMessage) {}
    }

    interface Interactor {
        fun getBanks(initDate:String, finalDate:String, type:String, apiCallbackDefault: APICallbackDefault<NegotiationsBanks, String>)
        fun cleanDisposable()
        fun resumeDisposable()
    }
}