package br.com.mobicare.cielo.cancelSale.data.mapper

import br.com.mobicare.cielo.cancelSale.data.model.response.BalanceInquiryResponse
import br.com.mobicare.cielo.cancelSale.data.model.response.CancelSaleResponse
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale

object CancelSaleAPIMapper {
    fun mapToBalanceInquiryResponse(response: BalanceInquiryResponse?): BalanceInquiry? {
        return response?.let {
            BalanceInquiry(
                response.authorizationCode,
                response.availableAmount,
                response.cardBrandCode,
                response.eligible,
                response.grossAmount,
                response.id,
                response.imgCardBrand,
                response.logicalNumber,
                response.nsu,
                response.paymentTypeDescription,
                response.productCode,
                response.saleDate,
                response.tid,
                response.truncatedCardNumber
            )
        }
    }

    fun mapToCancelSaleResponse(response: CancelSaleResponse?): CancelSale? {
        return response?.let {
            CancelSale(
                response.requestId,
                response.errorCode,
                response.errorMessage
            )
        }
    }
}