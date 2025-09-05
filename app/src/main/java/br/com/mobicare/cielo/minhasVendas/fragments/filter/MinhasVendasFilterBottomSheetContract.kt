package br.com.mobicare.cielo.minhasVendas.fragments.filter

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface MinhasVendasFilterBottomSheetContract {

    interface View {
        fun loadingState()
        fun hideLoading()
        fun logout(msg: ErrorMessage)
        fun showError()
        fun showCardBrands(cardBrands: List<ItemSelectable<CardBrand>>)
        fun showPaymentTypes(paymentTypes: List<ItemSelectable<PaymentType>>)
        fun applyFilter(quickFilter: QuickFilter)
        fun loadMoreFilters(quickFilter: QuickFilter?)
        fun loadNsuAndAuthorizationCode(quickFilter: QuickFilter?)
        fun showCancelInputs()

    }

    interface Presenter {
        fun load(quickFilter: QuickFilter, isLoadPaymentsType: Boolean = false)
        fun applyFilter(inputQuickFilter: QuickFilter? = null)
        fun cleanFilter()
        fun onPause()
        fun onDestroy()
    }

}