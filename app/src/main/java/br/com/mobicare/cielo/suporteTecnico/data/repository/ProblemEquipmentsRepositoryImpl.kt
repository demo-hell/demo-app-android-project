package br.com.mobicare.cielo.suporteTecnico.data.repository

import br.com.mobicare.cielo.suporteTecnico.data.dataSource.ProblemEquipmentsDataSource
import br.com.mobicare.cielo.suporteTecnico.domain.repo.ProblemEquipmentsRepository

class ProblemEquipmentsRepositoryImpl(
    private val dataSource: ProblemEquipmentsDataSource
): ProblemEquipmentsRepository {

    override suspend fun getProblemEquipments() = dataSource.getProblemEquipments()
    override suspend fun getEligibility(technology: String, code: String) = dataSource.getEligibility(technology, code)
}