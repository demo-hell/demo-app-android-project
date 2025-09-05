package br.com.mobicare.cielo.simulator.simulation.presentation.state

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts

sealed class UiSimulatorProductState {
    object ShowLoading : UiSimulatorProductState()

    object HideLoading : UiSimulatorProductState()

    data class Success(
        val simulatorProducts: SimulatorProducts
    ) : UiSimulatorProductState()

    data class Error(
        val error: NewErrorMessage? = null
    ) : UiSimulatorProductState()
}