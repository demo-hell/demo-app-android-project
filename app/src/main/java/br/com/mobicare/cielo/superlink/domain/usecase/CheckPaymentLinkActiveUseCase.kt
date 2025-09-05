package br.com.mobicare.cielo.superlink.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse
import br.com.mobicare.cielo.superlink.domain.repository.SuperLinkRepository

class CheckPaymentLinkActiveUseCase(
    private val repository: SuperLinkRepository
) : UseCaseWithoutParams<PaymentLinkResponse> {
    override suspend fun invoke() = repository.isPaymentLinkActive()
}

