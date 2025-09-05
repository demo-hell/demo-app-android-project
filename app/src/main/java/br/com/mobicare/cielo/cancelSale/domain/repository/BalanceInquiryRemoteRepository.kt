package br.com.mobicare.cielo.cancelSale.domain.repository

import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface BalanceInquiryRemoteRepository {
    suspend fun balanceInquiry(balanceInquiryRequest: BalanceInquiryRequest): CieloDataResult<BalanceInquiry>
}