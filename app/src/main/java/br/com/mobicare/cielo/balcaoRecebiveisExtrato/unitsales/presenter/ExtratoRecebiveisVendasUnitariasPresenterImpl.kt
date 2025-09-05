package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter

import android.content.Context
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment.ExtratoRecebiveisVendasUnitariasView
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.toNewErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.rxkotlin.addTo

class ExtratoRecebiveisVendasUnitariasPresenterImpl(
    private val context: Context,
    private val view: ExtratoRecebiveisVendasUnitariasView,
    private val repository: BalcaoRecebiveisExtratoContract.Interactor
) : ExtratoRecebiveisVendasUnitariasPresenter {

    private var negotiationItem: Item? = null
    private val disposable = CompositeDisposableHandler()
    private val PAGE_SIZE = 25
    private var quickFilter: QuickFilter? = null

    override fun onCreate(
        negotiationItem: Item,
        quickFilter: QuickFilter?
    ) {
        this.negotiationItem = negotiationItem
        this.quickFilter = quickFilter
        getUnitReceivable(1, false)
    }

    override fun getUnitReceivable(
        page: Int,
        isLoadingMore: Boolean
    ) {

        var data = ArrayList<Int>()
        quickFilter?.listBrandSales?.let { itList ->
            itList.forEach { itCard ->
                itCard.value?.let { data.add(itCard.value.toInt()) }
            }
        }

        repository.getUnitReceivable(
            page,
            PAGE_SIZE,
            negotiationItem?.date ?: "",
            negotiationItem?.operationNumber ?: "",
            quickFilter?.initialDate,
            quickFilter?.finalDate,
            quickFilter?.identificationNumber,
            data
        )
            .configureIoAndMainThread()
            .doOnSubscribe { if (isLoadingMore) view.showLoadingMore() else view.showLoading() }
            .doFinally { if (isLoadingMore) view.hideLoadingMore() else view.hideLoading() }
            .subscribe({
                view.onSuccess(ExtratoRecebiveisVendasUnitariasMapper.mapper(context, it))
            }, {
                view.onError(APIUtils.convertToErro(it))
            })
            .addTo(disposable.compositeDisposable)
    }

    override fun onResume(negotiationType: Int?) {
        trackScreenViewEvent(negotiationType)
        disposable.start()
    }

    private fun trackScreenViewEvent(negotiationType: Int?) {
        view.logScreenView(getScreenName(negotiationType))
    }

    override fun trackException(negotiationType: Int?, error: ErrorMessage) {
        view.logException(getScreenName(negotiationType), error.toNewErrorMessage())
    }

    private fun getScreenName(negotiationType: Int?): String {
        return when (negotiationType) {
            ONE -> ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO_SEE_MORE_OPERATION_DETAILS
            else -> ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET_SEE_MORE_OPERATION_DETAILS
        }
    }

    override fun onPause() {
        disposable.destroy()
    }
}