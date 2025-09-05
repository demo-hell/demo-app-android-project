package br.com.mobicare.cielo.extrato.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.ExtratoListaTransicaoObj

interface ExtratoListaAcumuladaContract{
    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showError(error: ErrorMessage)
        fun loadFooter(quantity: Int, amount: String?)
        fun loadList(transactions: ArrayList<ExtratoListaTransicaoObj>?)
        fun appendList(transactions: ArrayList<ExtratoListaTransicaoObj>)
        fun addScrollEvent()
        fun removeScrollEvent()
        fun logout(erro: String?)
        fun showEmpty(msgId: Int)
    }

    interface Presenter {
        fun callAPI(initialDate: String?, finalDate: String?, period: String?)
    }
}