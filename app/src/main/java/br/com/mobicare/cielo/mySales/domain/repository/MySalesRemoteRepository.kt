package br.com.mobicare.cielo.mySales.domain.repository

import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO

interface MySalesRemoteRepository {
    suspend fun getSummarySales(params: GetSalesDataParams): CieloDataResult<SummarySalesBO>
    suspend fun getCanceledSales(params: GetCanceledSalesParams): CieloDataResult<CanceledSummarySalesBO>
    suspend fun getSummarySalesHistory(params: GetSalesHistoryParams): CieloDataResult<ResultSummarySalesHistoryBO>
    suspend fun getMySalesTransactions(params: GetSalesDataParams): CieloDataResult<SummarySalesBO>
    suspend fun getSaleMerchant(params: GetMerchantParams): CieloDataResult<SalesMerchantBO>
}