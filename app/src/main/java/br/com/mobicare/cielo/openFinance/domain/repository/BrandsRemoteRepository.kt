package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.Brand

interface BrandsRemoteRepository {
    suspend fun getBrands(name: String): CieloDataResult<List<Brand>>
}