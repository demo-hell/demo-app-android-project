package br.com.mobicare.cielo.chargeback.data.model.request

interface OnResultFilterListener {

    fun onResultFilterListener(chargebackListParams: ChargebackListParams,numberOfAppliedFilters: Int)

    fun onClearFilterListener(chargebackStatus: String)

}