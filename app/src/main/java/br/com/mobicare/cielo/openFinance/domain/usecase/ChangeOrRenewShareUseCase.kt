package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.openFinance.domain.repository.ChangeOrRenewShareRemoteRepository

class ChangeOrRenewShareUseCase(private val repository: ChangeOrRenewShareRemoteRepository) {
    suspend operator fun invoke(changeOrRenewShareRequest: ChangeOrRenewShareRequest):
            CieloDataResult<ChangeOrRenewShare> {
        return repository.changeOrRenewShare(changeOrRenewShareRequest)
    }
}