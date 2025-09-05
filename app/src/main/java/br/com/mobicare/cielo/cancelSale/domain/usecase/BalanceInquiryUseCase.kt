package br.com.mobicare.cielo.cancelSale.domain.usecase

import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.repository.BalanceInquiryRemoteRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class BalanceInquiryUseCase(private val repository: BalanceInquiryRemoteRepository) {
    suspend operator fun invoke(balanceInquiryRequest: BalanceInquiryRequest): CieloDataResult<BalanceInquiry> {
        return repository.balanceInquiry(balanceInquiryRequest)
    }
}