package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixExtractRepository

class GetPixExtractUseCase(
    private val repository: PixExtractRepository
) {

    suspend operator fun invoke(request: PixExtractFilterRequest): CieloDataResult<PixExtract> {
        return repository.getExtract(request)
    }

}