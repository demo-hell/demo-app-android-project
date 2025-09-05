package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse
import br.com.mobicare.cielo.openFinance.domain.repository.SharedDataConsentsRemoteRepository

class SharedDataConsentsUseCase(private val repository: SharedDataConsentsRemoteRepository) {
    suspend operator fun invoke(
        journey: String,
        page: String,
        pageSize: String?
    ): CieloDataResult<SharedDataConsentsResponse> {
        return repository.getConsents(journey, page, pageSize)
    }
}