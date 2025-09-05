package br.com.mobicare.cielo.internaluser.presenter

import br.com.mobicare.cielo.changeEc.domain.Merchant

interface InternalUserPresenter {

    fun searchChild(page: Int, searchCriteria: String?)
    fun callChangeEc(merchant: Merchant, token: String,fingerprint: String)
}