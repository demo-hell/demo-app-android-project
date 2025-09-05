package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply

import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration

interface SelfServiceSupplyContract {
    interface View : BaseView {
        fun show(supplies: List<ItemBannerMigration>)
        fun openCoinEngine(tagName: String, supplies: List<SupplyDTO>)
        fun openSuppliesEngine(tagName: String, supplies: List<SupplyDTO>)
        fun showIneligibleUser(message: String)
    }

    interface Presenter {
        fun load()
        fun onPause()
        fun selectItem(items: ItemBannerMigration)
    }
}