package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest
import br.com.mobicare.cielo.openFinance.domain.repository.EndShareRemoteRepository

class EndShareUseCase(private val repository: EndShareRemoteRepository) {
    suspend operator fun invoke(
        otpCode: String,
        request: EndShareRequest
    ): CieloDataResult<Any> {
        return repository.endShare(otpCode, request)
    }
}