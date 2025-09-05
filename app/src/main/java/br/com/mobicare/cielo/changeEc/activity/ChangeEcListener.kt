package br.com.mobicare.cielo.changeEc.activity

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj

interface ChangeEcListener {
    fun onNextStep(isFinish: Boolean)
    fun onBackStep()
    fun onLogout()
    fun onMerchant(merchant: Merchant)

    fun onButtonLeftVisible(isVisible : Boolean)

    fun showLoading() { }
    fun hideLoading() { }
    fun onChangeNewEc(impersonate: Impersonate, merchant: Merchant)
}