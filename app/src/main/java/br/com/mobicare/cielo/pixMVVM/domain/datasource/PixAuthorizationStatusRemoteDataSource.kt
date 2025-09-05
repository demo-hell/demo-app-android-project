package br.com.mobicare.cielo.pixMVVM.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus

interface PixAuthorizationStatusRemoteDataSource {
    suspend fun getPixAuthorizationStatus(): CieloDataResult<PixAuthorizationStatus>
}