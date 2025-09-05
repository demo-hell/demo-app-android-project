package br.com.mobicare.cielo.mySales.data.mapper

import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummarySalesHistory
import br.com.mobicare.cielo.mySales.data.model.responses.SummarySalesResponse
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.CardBrandsBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse

object SalesMapper {

    fun mapToSummarySalesBO(summarySalesResponse: SummarySalesResponse?): SummarySalesBO? {
        summarySalesResponse?.let { response ->
            return SummarySalesBO(
                summary =  response.summary,
                pagination = response.pagination,
                items = response.items
            )
        }
        return null
    }

    fun mapToCanceledSummarySalesBO(
        canceledSummarySalesResponse: ResultSummaryCanceledSales?): CanceledSummarySalesBO? {
            canceledSummarySalesResponse?.let { response ->
                return CanceledSummarySalesBO(
                    summary = response.summary,
                    pagination = response.pagination,
                    items = response.items
                )
            }
            return null
    }

    fun mapToResultSummarySalesHistoryBO(
        historySalesResponse: ResultSummarySalesHistory?): ResultSummarySalesHistoryBO? {
        historySalesResponse?.let { response ->
            return ResultSummarySalesHistoryBO(
                summary = response.summary,
                pagination = response.pagination,
                items = response.items
            )

        }
        return null
    }


    fun mapToSaleMerchantBO(merchantResponse: UserOwnerResponse?): SalesMerchantBO? {
        merchantResponse?.let {
            return SalesMerchantBO(
                address = merchantResponse.addresses.first(),
                companyName = merchantResponse.companyName,
                cnpj = merchantResponse.cnpj
            )
        }
        return null
    }


    fun mapToCardBrandsBO(response: ResultCardBrands?): CardBrandsBO? {
        response?.let {
            return CardBrandsBO(
                cardBrands =  it.cardBrands
            )
        }
        return null
    }

    fun mapToResultPaymentTypesBO(response: ResultPaymentTypes? ): ResultPaymentTypesBO? {
        response?.let {
            return ResultPaymentTypesBO(
                cardBrands = it.cardBrands,
                paymentTypes = it.paymentTypes
            )
        }
        return null
    }


}