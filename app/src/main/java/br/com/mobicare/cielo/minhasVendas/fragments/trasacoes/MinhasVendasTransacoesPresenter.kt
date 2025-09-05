package br.com.mobicare.cielo.minhasVendas.fragments.trasacoes

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.minhasVendas.domain.ResultSummarySales
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository
import java.text.SimpleDateFormat

class MinhasVendasTransacoesPresenter(
        private val view: MinhasVendasTransacoesContract.View,
        private val repository: MinhasVendasRepository,
        private val userPreferences: UserPreferences) : MinhasVendasTransacoesContract.Presenter {

    private var pageNumber: Int? = 1
    private var quickFilter: QuickFilter? = null
    private var isByRefreshing: Boolean = false
    private var sales = ArrayList<Sale>()
    private var lastPage: Boolean? = false

    override fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean) {
        this.quickFilter = filter
        this.sales.clear()
        this.setToolbarText()
        this.view.changeColorFilter(isFilterNotSelected())
        this.load(filter, isByRefreshing, 1)
    }

    override fun loadMore() {
        if (lastPage?.not() == true) {
            this.sales.last().let {
                this.quickFilter?.let { itFilter ->
                    this.pageNumber?.let { itPageNumber ->
                        this.view.showLoadingMoreItens()
                        this.load(itFilter, true, itPageNumber + 1)
                    }
                }
            }
        }
    }

    override fun refresh(filter: QuickFilter?) {
        filter?.let {
            this.loadInitial(it, true)
        }
    }

    override fun retry() {
        this.quickFilter?.let {
            this.pageNumber?.let { itPageNumber ->
                this.load(it, this.isByRefreshing, itPageNumber)
            }
        }
    }

    override fun onClearRequests() {
        this.repository.disposable()
    }

    private fun setToolbarText() {
        this.quickFilter?.let { itFilter ->
            itFilter.initialDate?.let { itDate ->
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val sdfPtBr = SimpleDateFormat("dd/MM/yyyy")
                val dateFormated = sdfPtBr.format(sdf.parse(itDate))
                this.view.setToolbarText(dateFormated)
            }
        }
    }

    private fun load(filter: QuickFilter, isByRefreshing: Boolean, pageNumber: Int? = null) {
        filter.initialDate?.let { itInitialDate ->
            filter.finalDate?.let { itFinalDate ->
                if (!isByRefreshing) {
                    this.view.showLoading()
                }
                val token = userPreferences.token
                val authorization = Utils.authorization()
                this.pageNumber = pageNumber
                this.isByRefreshing = isByRefreshing

                this.repository.getSummarySales(
                        accessToken = token,
                        authorization = authorization,
                        initialDate = itInitialDate,
                        finalDate = itFinalDate,
                        initialAmount = filter.initialAmount,
                        finalAmount = filter.finalAmount,
                        customId = filter.customId,
                        saleCode = filter.saleCode,
                        truncatedCardNumber = filter.truncatedCardNumber,
                        cardBrands = filter.cardBrand,
                        paymentTypes = filter.paymentType,
                        terminal = filter.terminal,
                        status = filter.status,
                        cardNumber = filter.cardNumber,
                        nsu = filter.nsu,
                        authorizationCode = filter.authorizationCode,
                        page = pageNumber,
                        pageSize = 25,
                        callback = object : APICallbackDefault<ResultSummarySales, String> {
                            override fun onError(error: ErrorMessage) {
                                if (error.logout) {
                                    //this@MinhasVendasTransacoesPresenter.view.logout(error)
                                } else {
                                    this@MinhasVendasTransacoesPresenter.view.showError(error)
                                }
                            }

                            override fun onSuccess(response: ResultSummarySales) {
                                this@MinhasVendasTransacoesPresenter.lastPage = response.pagination?.lastPage
                                this@MinhasVendasTransacoesPresenter.sales.addAll(response.items)
                                if (isByRefreshing) {
                                    this@MinhasVendasTransacoesPresenter.view.hideRefreshLoading()
                                } else {
                                    this@MinhasVendasTransacoesPresenter.view.hideLoading()
                                }
                                if (pageNumber != null && pageNumber > 1) {
                                    this@MinhasVendasTransacoesPresenter.view.showMoreSales(response.items)
                                } else {
                                    if (response.items.isNullOrEmpty()) {
                                        this@MinhasVendasTransacoesPresenter.view.showEmptyResult()
                                    } else {
                                        this@MinhasVendasTransacoesPresenter.view.showSummary(response.summary)
                                        this@MinhasVendasTransacoesPresenter.view.showSales(this@MinhasVendasTransacoesPresenter.sales)
                                    }
                                }
                            }

                        })
            }
        }
    }

    override fun showMoreFilters() {
        view.showMoreFilters(this.quickFilter)
    }

    fun isFilterNotSelected(): Boolean {
        quickFilter?.let { filter ->
            return (filter.cardBrand.isNullOrEmpty() && filter.paymentType.isNullOrEmpty() && (filter.nsu.isNullOrEmpty() && filter.authorizationCode.isNullOrEmpty() && filter.tid.isNullOrEmpty() && filter.truncatedCardNumber.isNullOrEmpty() && filter.softDescriptor.isNullOrEmpty() && filter.initialAmount == null && filter.finalAmount == null))
        }

        return true
    }
}