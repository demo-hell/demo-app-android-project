package br.com.mobicare.cielo.simulator.util

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.simulator.simulation.domain.model.Installment
import br.com.mobicare.cielo.simulator.simulation.domain.model.PaymentType
import br.com.mobicare.cielo.simulator.simulation.domain.model.Product
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts

object SimulatorFactory {

    val resultError = CieloDataResult.APIError(
        CieloAPIException.networkError(EMPTY)
    )

    val productsSuccess = SimulatorProducts(
        merchant = "2012359935", products = listOf(
            Product(
                cardBrandCode = "11", cardBrandName = "AGIPLAN", paymentTypes = listOf(
                    PaymentType(
                        productCode = 2, fastRepay = false, productDescription = "Parcelado loja"
                    )
                )
            ), Product(
                cardBrandCode = "1", cardBrandName = "VISA", paymentTypes = listOf(
                    PaymentType(
                        productCode = 40, fastRepay = false, productDescription = "Crédito À Vista"
                    ), PaymentType(
                        productCode = 41, fastRepay = false, productDescription = "Débito"
                    ), PaymentType(
                        productCode = 43, fastRepay = false, productDescription = "Parcelado loja"
                    )
                )
            )
        )
    )

    val simulationSuccess = listOf(
        Simulation(
            cardBrandCode = 2,
            productCode = 12,
            customerTransferIndicator = "N",
            installments = listOf(
                Installment(
                    installmentNumber = 2,
                    mdrTax = 1.15,
                    finalMdrTax = 1.15,
                    totalValue = 3333.33,
                    saleAmount = 3372.11,
                    customerInstallmentValue = 1666.67,
                    shopkeeperInstallmentValue = 1647.5,
                    receivableValue = 3295.0
                ), Installment(
                    installmentNumber = 6,
                    mdrTax = 1.15,
                    finalMdrTax = 1.15,
                    totalValue = 3333.33,
                    saleAmount = 3372.11,
                    customerInstallmentValue = 555.56,
                    shopkeeperInstallmentValue = 549.17,
                    receivableValue = 3295.0
                )
            ),
            receivableRemainingDays = "12",
            saleAmount = 3333.33,
            flexibleTerm = false
        )
    )

}