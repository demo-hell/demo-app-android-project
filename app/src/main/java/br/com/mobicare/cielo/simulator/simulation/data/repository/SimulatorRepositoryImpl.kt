package br.com.mobicare.cielo.simulator.simulation.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.simulator.simulation.data.datasource.remote.SimulatorRemoteDataSource
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts
import br.com.mobicare.cielo.simulator.simulation.domain.repository.SimulatorRepository

class SimulatorRepositoryImpl(
    private val remoteDataSource: SimulatorRemoteDataSource
) : SimulatorRepository {
    override suspend fun getSimulatorProducts(): CieloDataResult<SimulatorProducts> =
        remoteDataSource.getSimulationProducts()

    override suspend fun getSimulation(
        productTypeCode: Int?,
        fastReceiveIndicator: Boolean?,
        installmentAmount: Int?,
        salesValue: Double?
    ): CieloDataResult<List<Simulation>> =
        remoteDataSource.getSimulation(
            productTypeCode, fastReceiveIndicator, installmentAmount, salesValue
        )

}
