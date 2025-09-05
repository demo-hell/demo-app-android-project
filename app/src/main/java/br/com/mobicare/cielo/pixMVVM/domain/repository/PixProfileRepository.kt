package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest

interface PixProfileRepository {
    suspend fun update(otpCode: String?, request: PixProfileRequest): CieloDataResult<String>
}