package br.com.mobicare.cielo.pix.ui.extract.home.tabs

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.FilterExtract
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import br.com.mobicare.cielo.pix.domain.ReceiptsTab

interface PixExtractTabsContract {

    interface View : BaseView {
        var balance: String

        fun showExtract(
            extract: PixExtractResponse? = null,
            isFirstPage: Boolean = true,
            isFilter: Boolean = false
        )

        fun showError(error: ErrorMessage? = null, isFirstPage: Boolean = true)
        fun showFooter()
        fun showNoDataWithFilter() {}
        fun showDetails(pixExtractReceipt: PixExtractReceipt?)
        fun showMoreFilters(filter: FilterExtract?) {}
    }

    interface Presenter {
        fun getExtract(
            isFirstPage: Boolean = true,
            receiptsTab: ReceiptsTab,
            filter: FilterExtract? = null,
            isFilter: Boolean = false
        )

        fun onCreate()
        fun onDestroyView()
        fun onDestroy()
        fun showMoreFilters() {}
    }
}