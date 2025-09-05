package br.com.mobicare.cielo.suporteTecnico.domain.useCase

import br.com.mobicare.cielo.suporteTecnico.domain.repo.ProblemEquipmentsRepository

class GetProblemEquipmentsUseCase(
    private val repository: ProblemEquipmentsRepository
) {

    suspend fun getProblemEquipments() =
        repository.getProblemEquipments()

    suspend fun getEligibility(technology: String, code: String) =
        repository.getEligibility(technology, code)
}