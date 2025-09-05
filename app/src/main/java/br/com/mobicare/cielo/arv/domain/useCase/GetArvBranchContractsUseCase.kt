package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvBranchesContracts
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvBranchContractsUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(): CieloDataResult<ArvBranchesContracts> =
        repository.getBranchesContracts()
}