package br.com.mobicare.cielo.tapOnPhone.data.source

import br.com.mobicare.cielo.tapOnPhone.data.api.TapOnPhoneAPI

class RemoteTapOnPhoneEligibilityDataSource(private val api: TapOnPhoneAPI) {

    fun getEligibilityStatus() = api.getEligibilityStatus()
}