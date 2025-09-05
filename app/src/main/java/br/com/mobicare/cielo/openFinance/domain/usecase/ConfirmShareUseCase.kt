package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.domain.repository.ConfirmShareRemoteRepository

class ConfirmShareUseCase(private val repository: ConfirmShareRemoteRepository) {
    suspend operator fun invoke(confirmShareRequest: ConfirmShareRequest): CieloDataResult<ConfirmShare> {
        return repository.confirmShare(confirmShareRequest)
    }
}