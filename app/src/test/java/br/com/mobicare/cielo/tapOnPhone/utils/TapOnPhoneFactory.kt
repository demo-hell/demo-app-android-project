package br.com.mobicare.cielo.tapOnPhone.utils

import br.com.mobicare.cielo.component.requiredDataField.data.model.response.*

object TapOnPhoneFactory {

    val offerResponseMock = OfferResponse(
        offer = Offer(
            id = "",
            description = "Produto Cielo Tap",
            settlementTerm = 2,
            products = listOf(
                Product(
                    id = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    settlementTerm = 2,
                    reference = "TAP-ON-PHONE",
                    brands = listOf(
                        Brand(
                            code = "1",
                            name = "name",
                            conditions = listOf(
                                Condition(
                                    type = "DEBIT",
                                    label = "label",
                                    mdr = 1.0,
                                    flexibleTermPaymentMDR = 1.0,
                                    rateContractedRR = 1.2,
                                ),
                                Condition(
                                    type = "CREDIT_IN_CASH",
                                    label = "label",
                                    mdr = 1.0,
                                    flexibleTermPaymentMDR = 1.5,
                                    rateContractedRR = 1.2,
                                ),
                                Condition(
                                    type = "CREDIT_IN_INSTALLMENTS",
                                    label = "label",
                                    mdr = 1.0,
                                    flexibleTermPaymentMDR = 1.1,
                                    rateContractedRR = 1.2,
                                    installments = listOf(
                                        Installment(
                                            installment = 1,
                                            mdr = 2.0,
                                            rateContractedRR = 2.1,
                                            flexibleTermPaymentMDR = 2.0
                                        ),
                                        Installment(
                                            installment = 2,
                                            mdr = 2.0,
                                            rateContractedRR = 2.1,
                                            flexibleTermPaymentMDR = 2.1
                                        ),
                                        Installment(
                                            installment = 3,
                                            mdr = 2.0,
                                            rateContractedRR = 2.1,
                                            flexibleTermPaymentMDR = 2.2
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )

}