package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixReceiptsScheduledRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled

interface PixExtractRepository {
    suspend fun getExtract(request: PixExtractFilterRequest): CieloDataResult<PixExtract>

    suspend fun getReceiptsScheduled(request: PixReceiptsScheduledRequest): CieloDataResult<PixReceiptsScheduled>
}
