package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvBanksUseCase(private val repository: ArvRepositoryNew) {

    suspend operator fun invoke(): CieloDataResult<List<ArvBank>> =
        repository.getArvBanks()

}