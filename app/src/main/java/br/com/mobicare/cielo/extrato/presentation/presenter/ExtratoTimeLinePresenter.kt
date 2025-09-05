package br.com.mobicare.cielo.extrato.presentation.presenter

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.CreditCard.DAY_10
import br.com.mobicare.cielo.commons.constants.CreditCard.DAY_30
import br.com.mobicare.cielo.commons.constants.FORMAT_DATE_AMERICAN
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.convertToExtrato
import br.com.mobicare.cielo.commons.utils.daysFrom
import br.com.mobicare.cielo.commons.utils.getDateCurrency
import br.com.mobicare.cielo.extrato.data.managers.StatementRepository
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.PaginationObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTimeLineObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoTimeLineContract
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class ExtratoTimeLinePresenter(private var view: ExtratoTimeLineContract.View,
                               private var statementRepository: StatementRepository,
                               private val uiScheduler: Scheduler,
                               private val ioScheduler: Scheduler) :
        ExtratoTimeLineContract.Presenter, APICallbackDefault<ExtratoTimeLineObj, ErrorMessage> {

    var nextPage: Int = 1
    private var paginationId: String = ""

    private var withFooter: Boolean = false
    private var subs = CompositeDisposable()

    private var pagination: PaginationObj = PaginationObj().apply {
        this.pageSize = 25
        this.pageNumber = 1
        this.numPages = 1
    }

    override fun fetchStatements(initialDt: String, finalDt: String,
                                 pageSize: Int, page: Int, proxyCard: String) {

        MenuPreference.instance.getEC()?.let { ecNumber ->
            UserPreferences.getInstance().token.run {
                subs.add(statementRepository.statements(initialDt, finalDt, pageSize, page,
                        ecNumber,
                        this,
                        proxyCard)
                        .subscribeOn(ioScheduler)
                        .observeOn(uiScheduler)
                        .doOnSubscribe {
                            if (view.isAttached() && nextPage == 1)
                                view.showProgress()

                        }.doAfterTerminate {
                            if (view.isAttached())
                                view.hideProgress()

                        }.map {
                            val extractPage = ExtratoTimeLineObj()
                            extractPage.pagination = this@ExtratoTimeLinePresenter.pagination

                            var totalApprovedAmount = 0.0
                            var totalApproved = 0
                            val mutableStmList = mutableListOf<ExtratoTransicaoObj>()
                            if (it.statements.isEmpty()) {
                                extractPage.pagination?.newPage = false
                            } else {
                                extractPage.pagination?.let { pagination ->
                                    pagination.numPages++
                                    pagination.newPage = true
                                }

                                it.statements.forEach { statement ->
                                    val element = statement.convertToExtrato()
                                    if (element.statusCode == ExtratoStatusDef.APROVADA.toString()){
                                        totalApproved++
                                        totalApprovedAmount += statement.amount
                                    }
                                    mutableStmList.add(element)
                                }
                            }
                            val objList = arrayListOf(*(mutableStmList.toTypedArray()))
                            extractPage.transactions = objList
                            extractPage.quantity = totalApproved
                            extractPage.totalAmount = Utils.formatValue(totalApprovedAmount)
                            extractPage

                        }.subscribe({ statement ->
                            handleSuccessResponse(statement)
                            this@ExtratoTimeLinePresenter.pagination.pageNumber = 2
                        }, {
                            onError(APIUtils.convertToErro(it))
                        }))
            }
        }
    }

    override fun callAPI(date: String?, proxyCard: String) {
        val dateFormat = getDateCurrency(FORMAT_DATE_AMERICAN)
        if (date.isNullOrBlank())
            fetchStatements(Date().daysFrom(DAY_30),
                    dateFormat,
                    DAY_10,
                    nextPage,
                    proxyCard
            )
        else
            fetchStatements(date.toString(),
                    dateFormat,
                    DAY_10,
                    ONE,
                    proxyCard
            )
    }

    override fun onStart() {
        if (view.isAttached() && nextPage == 1)
            view.showProgress()
    }

    override fun onError(error: ErrorMessage) {
        if (view.isAttached()) {
            view.hideProgress()
            if (error.logout)
                view.logout(error.message)
            else
                view.showError(error)
        }
    }

    override fun onFinish() {
        if (view.isAttached())
            view.hideProgress()
    }

    override fun onSuccess(response: ExtratoTimeLineObj) {
        handleSuccessResponse(response)
    }

    private fun handleSuccessResponse(response: ExtratoTimeLineObj) {
        if (view.isAttached()) {
            response.transactions?.let { transactions ->
                if (transactions.size > ZERO ) {
                    this.paginationId = response.paginationId

                    if (withFooter)
                        view.loadFooter(response.quantity, response.totalAmount)

                    response.pagination?.let { pagination ->
                        if (pagination.pageNumber == ONE)
                            view.loadTimeLine(transactions)
                        else
                            view.appendTimeLine(transactions)

                        if (pagination.newPage)
                            nextPage++
                        else
                            view.removeScrollEvent()

                    } ?: run {
                        view.appendTimeLine(transactions)
                        view.removeScrollEvent()
                    }

                } else
                    view.showEmptyMsg(R.string.extrato_meus_cartoes_empty)
            } ?: run {
                view.showEmptyMsg(R.string.extrato_meus_cartoes_empty)
            }
        }
    }
}