package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse

interface PixMerchantRemoteRepository {
    suspend fun getPixMerchantListOpenFinance(): CieloDataResult<List<PixMerchantListResponse>>
}