package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.domain.repository.UpdateShareRemoteRepository

class UpdateShareUseCase(private val repository: UpdateShareRemoteRepository) {
    suspend operator fun invoke(
        shareId: String,
        request: UpdateShareRequest
    ): CieloDataResult<UpdateShare> {
        return repository.updateShare(shareId, request)
    }
}