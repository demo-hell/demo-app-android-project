package br.com.mobicare.cielo.meusCartoes.presentation.ui

import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj

interface LastTransactionsContract {

    interface Presenter {
        fun fetchStatements(initialDt: String,
                            finalDt: String,
                            pageSize: Int = 25,
                            page: Int = 1,
                            proxyCard: String?)

        fun onResume()
        fun onPause()
    }

    interface View {
        fun showLoading()
        fun hideLoading()

        fun showError()
        fun showMessageNotTransactions()

        fun showTransactions(transactions: List<ExtratoTransicaoObj>?)
    }
}