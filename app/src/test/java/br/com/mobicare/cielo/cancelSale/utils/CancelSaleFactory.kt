package br.com.mobicare.cielo.cancelSale.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.cielo.libflue.util.ZERO_DOUBLE
import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.mySales.data.model.Sale

object CancelSaleFactory {

    val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    val resultEmpty = CieloDataResult.Empty()

    val requestBalanceInquiry = BalanceInquiryRequest(
        CancelSaleConstants.CARD_BRAND_CODE,
        CancelSaleConstants.AUTHORIZATION_CODE,
        CancelSaleConstants.NSU,
        CancelSaleConstants.TRUNCATED_CARD_NUMBER,
        CancelSaleConstants.AUTHORIZATION_DATE,
        CancelSaleConstants.AUTHORIZATION_DATE,
        CancelSaleConstants.PAYMENT_TYPE_CODE,
        CancelSaleConstants.GROSS_AMOUNT,
        ONE,
        TWENTY_FIVE
    )
    val responseBalanceInquiry = BalanceInquiry(
        "teste",
        ZERO_DOUBLE,
        ZERO,
        true,
        ZERO_DOUBLE,
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        ZERO,
        "teste",
        "teste",
        "teste"
    )

    val cancelSaleRequest =
        CancelSaleRequest(ZERO_DOUBLE, EMPTY, ZERO, ZERO, EMPTY, EMPTY, ZERO_DOUBLE, EMPTY)
    val cancelSaleResponse = CancelSale(ZERO.toBigInteger(), EMPTY, EMPTY)

    val sale = Sale(
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        ZERO_DOUBLE,
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        "teste",
        ZERO,
        "teste",
        "teste",
        "teste",
        ZERO,
        "teste",
        "teste",
        ZERO_DOUBLE,
        ZERO_DOUBLE,
        ZERO_DOUBLE,
        ZERO,
        ZERO_DOUBLE,
        "teste",
        "teste",
        ZERO_DOUBLE,
        ZERO_DOUBLE,
        "teste",
        ZERO,
        "teste",
        ZERO.toLong()
    )
}