package br.com.mobicare.cielo.simulator.simulation.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.domain.repository.SimulatorRepository

class GetSimulationUseCase(
    private val repository: SimulatorRepository,
) {
    suspend operator fun invoke(
        productTypeCode: Int?,
        fastReceiveIndicator: Boolean?,
        installmentAmount: Int?,
        salesValue: Double?
    ): CieloDataResult<List<Simulation>> = repository.getSimulation(
        productTypeCode, fastReceiveIndicator, installmentAmount, salesValue
    )
}
