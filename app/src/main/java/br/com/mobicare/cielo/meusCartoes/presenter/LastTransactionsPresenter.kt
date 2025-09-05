package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.convertToExtrato
import br.com.mobicare.cielo.extrato.data.managers.StatementRepository
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTimeLineObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.presentation.ui.LastTransactionsContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class LastTransactionsPresenter(private val view: LastTransactionsContract.View,
                                private val repository: StatementRepository,
                                private val menuPreference: MenuPreference,
                                private val userPreferences: UserPreferences,
                                private val uiScheduler: Scheduler,
                                private val ioScheduler: Scheduler) : LastTransactionsContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun fetchStatements(initialDt: String,
                                 finalDt: String,
                                 pageSize: Int,
                                 page: Int,
                                 proxyCard: String?) {

        proxyCard?.let { proxy ->
            disposible.add(repository.statements(initialDt = initialDt, finalDt = finalDt,
                    pageSize = pageSize, page = page, merchantId = menuPreference.getEC(),
                    accessToken = userPreferences.token, proxyCard = proxy)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe {
                        view.showLoading()
                    }
                    .doAfterTerminate {
                        view.hideLoading()
                    }
                    .map {
                        val extract = ExtratoTimeLineObj()
                        val mutableStmList = mutableListOf<ExtratoTransicaoObj>()
                        it.statements.forEach { statement ->
                            val element = statement.convertToExtrato()
                            mutableStmList.add(element)
                        }

                        val transactions = arrayListOf(*(mutableStmList.toTypedArray()))
                        extract.transactions = transactions
                        extract
                    }
                    .subscribe({ userLastTransactions ->
                        if (userLastTransactions.transactions?.size == 0)
                            view.showMessageNotTransactions()
                        else
                            view.showTransactions(userLastTransactions.transactions?.take(3))
                    }, {
                        view.showError()
                    }))
        } ?: run {
            view.hideLoading()
            view.showError()
        }
    }

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun onPause() {
        disposible.dispose()
    }
}