package br.com.mobicare.cielo.extrato.presentation.ui

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.minhasVendas.domain.ResultSummarySales
import br.com.mobicare.cielo.minhasVendas.domain.SellsCancelParametersRequest
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.online.MinhasVendasOnlineContract

interface UserCanceledSellsItemsContract {

    interface Presenter : CommonPresenter, MinhasVendasOnlineContract.Presenter {
        fun getCanceledSells(accessToken: String,
                             sellsCancelParametersRequest: SellsCancelParametersRequest)
    }

    interface View : BaseView {


        fun hideRefreshLoading()

        fun showMoreItems()
        fun showLoadingMoreItems()

        fun showEmptyResults()
        fun showCanceledSellsSummary(summary: Summary)
        fun showMoreCanceledSells(result: ResultSummarySales)
        fun showCanceledSells(result: ResultSummarySales)

        fun showFullsecError()
    }

}