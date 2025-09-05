package br.com.mobicare.cielo.simulator.simulation.presentation.state

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation

sealed class UiSimulatorResultState {
    object ShowLoading : UiSimulatorResultState()

    object HideLoading : UiSimulatorResultState()

    data class Success(
        val simulatorResult: Simulation
    ) : UiSimulatorResultState()

    data class Error(
        val error: NewErrorMessage? = null
    ) : UiSimulatorResultState()
}