package br.com.mobicare.cielo.cancelSale.data.repository

import br.com.mobicare.cielo.cancelSale.data.datasource.BalanceInquiryDataSource
import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.repository.BalanceInquiryRemoteRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class BalanceInquiryRepositoryImpl(private val dataSource: BalanceInquiryDataSource) :
    BalanceInquiryRemoteRepository {
    override suspend fun balanceInquiry(balanceInquiryRequest: BalanceInquiryRequest):
            CieloDataResult<BalanceInquiry> {
        return dataSource.balanceInquiry(balanceInquiryRequest)
    }
}