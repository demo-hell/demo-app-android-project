package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.ready

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class TapOnPhoneTerminalReadyPresenter(private val userPreferences: UserPreferences) :
    TapOnPhoneTerminalReadyContract {

    override fun onSaveVisualizationHistoric() {
        userPreferences.setSawTerminalScreenReady()
    }
}