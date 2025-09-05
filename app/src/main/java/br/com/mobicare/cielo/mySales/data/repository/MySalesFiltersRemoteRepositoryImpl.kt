package br.com.mobicare.cielo.mySales.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesFiltersRemoteDataSource
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.data.model.bo.CardBrandsBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesFiltersRemoteRepository

class MySalesFiltersRemoteRepositoryImpl(
    private val mySalesFiltersRemoteDataSource: MySalesFiltersRemoteDataSource
): MySalesFiltersRemoteRepository {


    override suspend fun getFilteredCardBrands(params: GetBrandsSalesFiltersParams): CieloDataResult<CardBrandsBO> {
        return mySalesFiltersRemoteDataSource.getFilteredCardBrands(params)
    }
    override suspend fun getFilteredPaymentTypes(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO> {
       return mySalesFiltersRemoteDataSource.getFilteredPaymentTypes(params)
    }

    override suspend fun getFilteredCanceledSells(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO> {
        return mySalesFiltersRemoteDataSource.getFilteredCanceledSells(params)
    }

}