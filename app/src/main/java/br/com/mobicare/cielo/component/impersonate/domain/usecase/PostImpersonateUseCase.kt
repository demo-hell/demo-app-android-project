package br.com.mobicare.cielo.component.impersonate.domain.usecase

import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.domain.repository.ImpersonateRepository

class PostImpersonateUseCase(
    private val repository: ImpersonateRepository
) {

    suspend operator fun invoke(
        ec: String,
        type: String,
        impersonateRequest: ImpersonateRequest
    ) = repository.postImpersonate(ec, type, impersonateRequest)

}