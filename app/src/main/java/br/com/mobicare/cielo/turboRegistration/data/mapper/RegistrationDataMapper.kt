package br.com.mobicare.cielo.turboRegistration.data.mapper

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO_TEXT
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.BankItem
import br.com.mobicare.cielo.turboRegistration.data.model.response.MccResponseItem
import br.com.mobicare.cielo.turboRegistration.data.model.response.OperationsResponseItem
import br.com.mobicare.cielo.turboRegistration.data.model.response.PurposeAddress
import br.com.mobicare.cielo.turboRegistration.data.model.response.SubMccResponse
import br.com.mobicare.cielo.turboRegistration.domain.model.Address
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.domain.model.Purpose
import br.com.mobicare.cielo.turboRegistration.domain.model.SubMcc


fun BankItem.toBank() =
    Bank(
        code = this.code ?: ZERO_TEXT,
        name = this.name.ifNullSimpleLine()
    )

fun MccResponseItem.toMcc(idAddress: String?) =
    Mcc(
        idAddress = idAddress,
        code = this.code,
        description = this.description.capitalizePTBR(),
        subMcc = this.subMcc.map { it.toSubBusinessLine() }
    )

fun SubMccResponse.toSubBusinessLine() =
    SubMcc(
        code = this.code,
        description = this.description.capitalizePTBR()
    )

fun SubMcc.subLineToLine(): Mcc {
    return Mcc(
        code = this.code,
        description = this.description.ifNullSimpleLine(),
        subMcc = null
    )
}

fun OperationsResponseItem.toOperation() =
    Operation(
        label = this.label.ifNullSimpleLine(),
        value = this.value.ifNullSimpleLine(),
        isLegalEntity = this.isLegalEntity,
        isSavingsAccount = this.isSavingsAccount
    )

fun AddressResponse.toAddress(cep: String?) = Address(
    city = this.city.capitalizeWords() ?: EMPTY,
    country = this.country.capitalizeWords() ?: EMPTY,
    description = this.description.capitalizeWords() ?: EMPTY,
    neighborhood = this.neighborhood.capitalizeWords() ?: EMPTY,
    number = this.number ?: EMPTY,
    purposeAddress = this.purposeAddress?.map { it.toPurpose() } ?: emptyList(),
    state = this.state ?: EMPTY,
    streetAddress = this.streetAddress.capitalizeWords() ?: EMPTY,
    streetAddress2 = this.streetAddress2.capitalizeWords() ?: EMPTY,
    types = this.types?.map { it.ifNullSimpleLine() } ?: emptyList(),
    zipCode = this.zipCode ?: cep.orEmpty()
)

fun PurposeAddress.toPurpose() = Purpose(
    type = this.type
)