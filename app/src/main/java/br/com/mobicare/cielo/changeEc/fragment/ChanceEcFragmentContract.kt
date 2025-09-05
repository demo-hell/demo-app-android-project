package br.com.mobicare.cielo.changeEc.fragment

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import java.util.*


interface ChanceEcFragmentContract {

    interface Presenter : BasePresenter<View> {
        fun loadItens()
        fun callChangeEc(merchant: Merchant, token: String, isLogar: Boolean, isMerchantNode: Boolean, callChildScreen: Boolean,fingerprint: String)
        fun getChildrens() {}
    }

    interface View : BaseView, IAttached {
        fun showMerchants(merchants: ArrayList<Merchant>) {}
        fun showChildren(merchants: MerchantsObj) {}
        fun callChildrenScreen(merchant: Merchant) {}
        fun onChangetoNewEc(merchant: Merchant, impersonate: Impersonate)
    }

}