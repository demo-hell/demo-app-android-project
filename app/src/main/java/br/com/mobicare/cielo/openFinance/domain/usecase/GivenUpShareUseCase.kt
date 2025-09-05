package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare
import br.com.mobicare.cielo.openFinance.domain.repository.GivenUpShareRemoteRepository

class GivenUpShareUseCase(private val repository: GivenUpShareRemoteRepository) {
    suspend operator fun invoke(givenUpShareRequest: GivenUpShareRequest): CieloDataResult<GivenUpShare> {
        return repository.givenUpShare(givenUpShareRequest)
    }
}