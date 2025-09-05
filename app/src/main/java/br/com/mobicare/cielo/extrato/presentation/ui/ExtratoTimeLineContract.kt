package br.com.mobicare.cielo.extrato.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj

interface ExtratoTimeLineContract {

    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showError(error: ErrorMessage)
        fun loadFooter(quantity: Int, amount: String?)
        fun loadTimeLine(transactions: ArrayList<ExtratoTransicaoObj>?)
        fun appendTimeLine(transactions: ArrayList<ExtratoTransicaoObj>)
        fun logout(error: String?)
        fun showEmptyMsg(msgId: Int)
        fun addScrollEvent()
        fun removeScrollEvent()
    }

    interface Presenter {
        fun fetchStatements(initialDt: String, finalDt: String, pageSize: Int = 10, page: Int = 1, proxyCard: String)
        fun callAPI(date: String?, proxyCard: String)
    }

}