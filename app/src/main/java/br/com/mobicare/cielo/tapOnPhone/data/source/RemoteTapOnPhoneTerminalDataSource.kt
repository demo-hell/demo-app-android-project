package br.com.mobicare.cielo.tapOnPhone.data.source

import br.com.mobicare.cielo.tapOnPhone.data.api.TapOnPhoneAPI
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalRequest

class RemoteTapOnPhoneTerminalDataSource(private val api: TapOnPhoneAPI) {

    fun activateTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest) =
        api.activateTerminal(deviceFingerPrint)

    fun createTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest) =
        api.createTerminal(deviceFingerPrint)

    fun getTerminalInfo(
        token: String,
        deviceFingerPrint: TapOnPhoneTerminalRequest
    ) = api.getTerminalInfo(token, deviceFingerPrint)
}