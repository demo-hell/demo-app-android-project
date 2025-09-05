package br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.model.PixBank

interface PixSelectBankContract {
    interface View: BaseView {
        fun onSelectedBank(bank: PixBank)
        fun showFilteredBanks(filteredBanks: MutableList<PixBank>)
        fun setupBankListView(banks: List<PixBank>)
    }

    interface Presenter {
        fun getAllBanks()
        fun fetchAllBanks(): List<PixBank>
        fun searchBank(bankCodeOrName: String)
        fun onResume()
        fun onPause()
    }
}