package br.com.mobicare.cielo.mySales.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesRemoteDataSource
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository

class MySalesRemoteRepositoryImpl(private val mySalesRemoteDataSource: MySalesRemoteDataSource):
    MySalesRemoteRepository {
    override suspend fun getSummarySales(params: GetSalesDataParams): CieloDataResult<SummarySalesBO> {
       return  mySalesRemoteDataSource.getSummarySales(params)
    }

    override suspend fun getCanceledSales(params: GetCanceledSalesParams): CieloDataResult<CanceledSummarySalesBO> {
        return mySalesRemoteDataSource.getCanceledSales(params)
    }

    override suspend fun getSummarySalesHistory(params: GetSalesHistoryParams): CieloDataResult<ResultSummarySalesHistoryBO> {
        return mySalesRemoteDataSource.getSummarySalesHistory(params)
    }

    override suspend fun getMySalesTransactions(params: GetSalesDataParams): CieloDataResult<SummarySalesBO> {
        return mySalesRemoteDataSource.getMySalesTransactions(params)
    }

    override suspend fun getSaleMerchant(params: GetMerchantParams): CieloDataResult<SalesMerchantBO> {
        return mySalesRemoteDataSource.getSaleMerchant(params)
    }
}