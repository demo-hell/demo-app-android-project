package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.repository.CreateShareRemoteRepository

class CreateShareUseCase(private val repository: CreateShareRemoteRepository) {
    suspend operator fun invoke(request: CreateShareRequest): CieloDataResult<CreateShare> {
        return repository.createShare(request)
    }
}