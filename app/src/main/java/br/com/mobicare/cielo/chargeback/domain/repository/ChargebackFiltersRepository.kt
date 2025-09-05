package br.com.mobicare.cielo.chargeback.domain.repository

import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ChargebackFiltersRepository {

    suspend fun getChargebackFilters(): CieloDataResult<ChargebackFilters>


}