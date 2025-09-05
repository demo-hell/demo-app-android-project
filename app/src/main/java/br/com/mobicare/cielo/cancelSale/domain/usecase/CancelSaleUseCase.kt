package br.com.mobicare.cielo.cancelSale.domain.usecase

import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.cancelSale.domain.repository.CancelSaleRemoteRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class CancelSaleUseCase(private val repository: CancelSaleRemoteRepository) {
    suspend operator fun invoke(
        otpCode: String,
        sales: ArrayList<CancelSaleRequest>
    ): CieloDataResult<CancelSale> {
        return repository.cancelSale(otpCode, sales)
    }
}