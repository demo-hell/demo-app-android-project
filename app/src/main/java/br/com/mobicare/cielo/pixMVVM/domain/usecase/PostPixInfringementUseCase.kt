package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository

class PostPixInfringementUseCase(
    private val repository: PixInfringementRepository
) {

    suspend operator fun invoke(request: PixCreateNotifyInfringementRequest): CieloDataResult<PixCreateNotifyInfringementResponse> {
        return repository.postInfringement(request)
    }

}