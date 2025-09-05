package br.com.mobicare.cielo.cancelSale.domain.repository

import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface CancelSaleRemoteRepository {
    suspend fun cancelSale(
        otpCode: String,
        sales: ArrayList<CancelSaleRequest>
    ): CieloDataResult<CancelSale>
}