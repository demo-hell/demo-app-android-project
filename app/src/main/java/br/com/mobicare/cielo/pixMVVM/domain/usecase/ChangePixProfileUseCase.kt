package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixProfileRepository

class ChangePixProfileUseCase(
    private val repository: PixProfileRepository
) : UseCase<ChangePixProfileUseCase.Params, String> {

    override suspend fun invoke(params: Params) = repository.update(params.token, params.request)

    data class Params(
        val token: String?,
        val request: PixProfileRequest
    )

}