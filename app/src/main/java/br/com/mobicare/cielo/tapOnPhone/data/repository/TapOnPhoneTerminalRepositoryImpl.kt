package br.com.mobicare.cielo.tapOnPhone.data.repository

import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneTerminalDataSource
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalRequest
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneTerminalRepository

class TapOnPhoneTerminalRepositoryImpl(private val dataSource: RemoteTapOnPhoneTerminalDataSource) :
    TapOnPhoneTerminalRepository {

    override fun activateTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest) =
        dataSource.activateTerminal(deviceFingerPrint)

    override fun createTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest) =
        dataSource.createTerminal(deviceFingerPrint)

    override fun getTerminalInfo(
        token: String,
        deviceFingerPrint: TapOnPhoneTerminalRequest
    ) = dataSource.getTerminalInfo(token, deviceFingerPrint)
}