package br.com.mobicare.cielo.commons.data.dataSource

import br.com.mobicare.cielo.tapOnPhone.data.api.TapOnPhoneAPI

class PosVirtualWhiteListDataSource(private val api: TapOnPhoneAPI) {
    fun getPosVirtualWhiteList() = api.getPosVirtualWhiteList()
}