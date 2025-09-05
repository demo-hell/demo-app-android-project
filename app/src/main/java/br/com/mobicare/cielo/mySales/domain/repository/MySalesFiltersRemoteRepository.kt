package br.com.mobicare.cielo.mySales.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.data.model.bo.CardBrandsBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO

interface MySalesFiltersRemoteRepository {
    suspend fun getFilteredCardBrands(params: GetBrandsSalesFiltersParams): CieloDataResult<CardBrandsBO>
    suspend fun getFilteredPaymentTypes(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO>
    suspend fun getFilteredCanceledSells(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO>

}