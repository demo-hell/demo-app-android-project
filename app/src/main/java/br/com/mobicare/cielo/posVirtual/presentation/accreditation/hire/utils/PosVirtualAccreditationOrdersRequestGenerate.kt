package br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire.utils

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Order
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.PayoutData
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.TargetBankAccount
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants
import br.com.mobicare.cielo.posVirtual.domain.model.BankUI
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Agreement as AgreementRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Agreement as AgreementResponse

object PosVirtualAccreditationOrdersRequestGenerate {

    fun generate(
        offerID: String,
        sessionID: String?,
        bank: BankUI?,
        agreements: List<AgreementResponse>,
        itemsConfigurations: List<String>
    ) = OrdersRequest(
        type = RequiredDataFieldConstants.REQUIRED_DATA_FIELD_ORDER_TYPE,
        order = Order(
            offerId = offerID,
            payoutData = PayoutData(
                payoutMethod = PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_BANK_ACCOUNT,
                targetBankAccount = TargetBankAccount(
                    bankNumber = bank?.code.orEmpty(),
                    agency = bank?.onlyAgency.orEmpty(),
                    accountNumber = bank?.account.orEmpty(),
                    accountType = PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_CHECKING
                )
            ),
            agreements = agreements.map {
                AgreementRequest(
                    it.code.orEmpty(),
                    PosVirtualConstants.POS_VIRTUAL_ACCREDITATION_CREATE_ORDER_AUTHORIZED
                )
            },
            itemsConfigurations = itemsConfigurations,
            sessionId = sessionID
        ),
        registrationData = null
    )

}