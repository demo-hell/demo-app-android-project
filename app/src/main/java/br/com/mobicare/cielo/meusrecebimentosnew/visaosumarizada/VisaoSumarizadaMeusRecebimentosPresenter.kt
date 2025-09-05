package br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.MeusRecebimentosInteractor
import br.com.mobicare.cielo.meusrecebimentosnew.models.SummaryItems
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Pagination
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.SummaryViewResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class VisaoSumarizadaMeusRecebimentosPresenter(
        private val view: VisaoSumarizadaMeusRecebimentosContract.View,
        private val repository: MeusRecebimentosInteractor) :
        VisaoSumarizadaMeusRecebimentosContract.Presenter {

    private lateinit var disposible: CompositeDisposable

    private var summaryItems: SummaryItems? = null
    private var quickFilter: QuickFilter? = null
    private var pagination: Pagination? = null


    override fun onStart() {
        disposible = CompositeDisposable()
    }

    override fun onDestroy() {
        disposible.dispose()
    }

    override fun load(bundle: Bundle?) {
        processExternalParams(bundle)
        loadSummaryView(ONE)
    }

    override fun loadMore(customQuickFilter: QuickFilter?) {
        this.pagination?.let {
            if (it.pageNumber + ONE <= it.numPages) {
                customQuickFilter?.let { customQuickFilter ->
                    loadSummaryView((it.pageNumber + ONE).toInt(), isRefresh = true,
                            customQuickFilter = customQuickFilter)
                } ?: loadSummaryView((it.pageNumber + ONE).toInt(), isRefresh = true)
            } else {
                this.view.hideMoreLoading()
            }
        }
    }

    override fun helpButtonClicked() {
        this.summaryItems?.let {
            val code = if (it.code == ONE_HUNDRED) PENDING else it.code
            ConfigurationPreference.instance.getConfigurationValue("$SALES_RECEIVABLES_CODE_PREFIX${code}", EMPTY).let { itMessage ->
                this.view.showHelpPopup(it.code, it.type, itMessage)
            }
        }
    }

    override fun filterReceivables(customQuickFilter: QuickFilter?) {
        customQuickFilter?.let {
            view.showFilters(it)
        } ?: quickFilter?.let {
            view.showFilters(it)
        }
    }

    override fun applyFilter(quickFilter: QuickFilter) {
        loadSummaryView(1, customQuickFilter = quickFilter)
    }


    private fun processExternalParams(bundle: Bundle?) {
        bundle?.let {
            it.getParcelable<SummaryItems>(PARAM_OBJECT)?.let { itSummaryItems ->
                this.summaryItems = itSummaryItems
                this.view.configureToolbar(itSummaryItems.type)
            }
            it.getSerializable(PARAM_QUICKFILTER)?.let { itParam ->
                this.quickFilter = itParam as QuickFilter
                this.quickFilter?.let { itQuickFilter ->
                    itQuickFilter.initialDate?.let { itInitialDate ->
                        itQuickFilter.finalDate?.let { itFinalDate ->
                            this.view.showSubHeader(
                                    DateTimeHelper.convertToDate(itInitialDate, FORMAT_DATE_AMERICAN, FORMAT_DATE_PORTUGUESE)
                                    , DateTimeHelper.convertToDate(itFinalDate, FORMAT_DATE_AMERICAN, FORMAT_DATE_PORTUGUESE)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun loadSummaryView(page: Int, pageSize: Int, isRefresh: Boolean,
                                 customQuickFilter: QuickFilter?) {
        this.summaryItems?.let { itSummaryItems ->

            customQuickFilter?.let {
                loadWithCustomFilter(itSummaryItems, it, page, pageSize, isRefresh)
            } ?: this.quickFilter?.let { itQuickFilter ->
                loadWithCustomFilter(itSummaryItems, itQuickFilter, page, pageSize, isRefresh)
            }
        }
    }

    private fun loadWithCustomFilter(itSummaryItems: SummaryItems, itQuickFilter: QuickFilter,
                                     page: Int,
                                     pageSize: Int,
                                     isRefresh: Boolean) {
        val selfLink = itSummaryItems.links.filter { it.rel == SELF_LINK_FIELD }
        if (selfLink.isNotEmpty()) {
            this.repository.getSummaryView(
                    selfLink.first().href,
                    itQuickFilter.initialDate,
                    itQuickFilter.finalDate,
                    cardBrands = itQuickFilter.cardBrand,
                    paymentTypes = itQuickFilter.paymentType,
                    page = page,
                    pageSize = pageSize)
                    .configureIoAndMainThread()
                    .doOnSubscribe { if (page == ONE && !isRefresh) view.showLoading() }
                    .doFinally { if (page == ONE && !isRefresh) view.hideLoading() }
                    .subscribe({
                        processResponse(it)
                    }, {
                        view.showError(APIUtils.convertToErro(it))
                    }).addTo(disposible)
        }
    }

    private fun processResponse(response: SummaryViewResponse) {
        this.pagination = response.pagination
        this.view.hideRefreshLoading()
        val amount = response.summary.totalNetAmount ?: response.summary.totalAmount ?: ZERO_DOUBLE
        this.view.showBottom(amount.toPtBrWithNegativeRealString(), if (amount > ZERO.toFloat()) R.color.green else R.color.red)
        this.pagination?.let { itPagination ->
            this.summaryItems?.let {

                enableFilterActionByCode(it)

                if (itPagination.pageNumber == ONE.toLong()) {
                    view.showItems(it.code, ArrayList(response.items), false, quickFilter)
                } else {
                    view.showMoreItems(it.code, ArrayList(response.items), itPagination.pageNumber == itPagination.numPages)
                }
            }
        }
    }

    private fun enableFilterActionByCode(it: SummaryItems) {
        view.enableFilterOnMenu(it.code)
    }

    private companion object {
        const val PENDING = "PENDINGS"
        const val SALES_RECEIVABLES_CODE_PREFIX = "SALES_RECEIVABLES_"
        const val SELF_LINK_FIELD = "self"
    }
}