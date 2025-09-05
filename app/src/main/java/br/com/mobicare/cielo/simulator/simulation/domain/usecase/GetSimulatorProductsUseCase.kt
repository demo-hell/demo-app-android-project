package br.com.mobicare.cielo.simulator.simulation.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts
import br.com.mobicare.cielo.simulator.simulation.domain.repository.SimulatorRepository

class GetSimulatorProductsUseCase(
    private val repository: SimulatorRepository,
) {
    suspend operator fun invoke(): CieloDataResult<SimulatorProducts> = repository.getSimulatorProducts()
}
