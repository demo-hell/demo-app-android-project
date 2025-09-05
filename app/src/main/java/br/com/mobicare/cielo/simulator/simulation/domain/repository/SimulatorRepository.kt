package br.com.mobicare.cielo.simulator.simulation.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts

interface SimulatorRepository {

    suspend fun getSimulatorProducts(): CieloDataResult<SimulatorProducts>
    suspend fun getSimulation(
        productTypeCode: Int?,
        fastReceiveIndicator: Boolean?,
        installmentAmount: Int?,
        salesValue: Double?
    ): CieloDataResult<List<Simulation>>
}
