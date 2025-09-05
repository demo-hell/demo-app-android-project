package br.com.mobicare.cielo.mySales.data.model.bo

import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.SaleCardBrand

data class ResultPaymentTypesBO(
    val cardBrands: List<SaleCardBrand>?,
    val paymentTypes: List<PaymentType>?
)