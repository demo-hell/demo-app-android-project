package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAuthorizationStatusRepository

class GetPixAuthorizationStatusUseCase(
    private val repository: PixAuthorizationStatusRepository
) : UseCaseWithoutParams<PixAuthorizationStatus> {

    override suspend fun invoke() = repository.getPixAuthorizationStatus()

}