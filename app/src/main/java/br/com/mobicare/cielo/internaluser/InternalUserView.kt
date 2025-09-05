package br.com.mobicare.cielo.internaluser

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface InternalUserView {
    fun showChildren(merchants: MerchantsObj)
    fun showLoading()
    fun showLoadingMore()
    fun hideLoading()
    fun hideLoadingMore()
    fun showError(error: ErrorMessage)
    fun onChangetoNewEc(merchant: Merchant, impersonate: Impersonate)
    fun showEmptyListError(visibility: Int, background: Int)
}