package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixDecodeQRCodeResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixOwnerType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQRCodeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode

fun PixDecodeQRCodeResponse.toEntity() =
    PixDecodeQRCode(
        endToEndId = endToEndId.orEmpty(),
        type = PixQRCodeType.find(type),
        pixType = PixQrCodeOperationType.find(pixType) ?: PixQrCodeOperationType.TRANSFER,
        participant = participant,
        participantName = participantName.orEmpty(),
        revision = revision,
        receiverName = receiverName.orEmpty(),
        receiverTradingName = receiverTradingName.orEmpty(),
        receiverPersonType = PixOwnerType.find(receiverPersonType.orEmpty()),
        receiverDocument = receiverDocument.orEmpty(),
        idTx = idTx.orEmpty(),
        payerName = payerName.orEmpty(),
        payerDocument = payerDocument.orEmpty(),
        city = city.orEmpty(),
        address = address.orEmpty(),
        state = state.orEmpty(),
        zipCode = zipCode.orEmpty(),
        originalAmount = originalAmount,
        interest = interest,
        penalty = penalty,
        discount = discount,
        abatement = abatement,
        finalAmount = finalAmount,
        withDrawAmount = withDrawAmount,
        changeAmount = changeAmount,
        allowsChange = allowsChange,
        expireDate = expireDate?.parseToZonedDateTime(),
        dueDate = dueDate.orEmpty(),
        daysAfterDueDate = daysAfterDueDate,
        creationDate = creationDate?.parseToZonedDateTime(),
        decodeDate = decodeDate?.clearDate()?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        url = url.orEmpty(),
        reusable = reusable,
        branch = branch.orEmpty(),
        accountType = accountType.orEmpty(),
        accountNumber = accountNumber.orEmpty(),
        key = key.orEmpty(),
        keyType = keyType.orEmpty(),
        category = category.orEmpty(),
        additionalData = additionalData.orEmpty(),
        payerType = PixOwnerType.find(payerType.orEmpty()),
        modalityAlteration = PixAllowsChangeValueEnum.find(modalityAlteration),
        description = description.orEmpty(),
        ispbWithDraw = ispbWithDraw,
        ispbWithDrawName = ispbWithDrawName.orEmpty(),
        modalityAltWithDraw = PixAllowsChangeValueEnum.find(modalityAltWithDraw),
        modalityWithDrawAgent = modalityWithDrawAgent.orEmpty(),
        ispbChange = ispbChange,
        ispbChangeName = ispbChangeName.orEmpty(),
        modalityAltChange = PixAllowsChangeValueEnum.find(modalityAltChange),
        modalityChangeAgent = modalityChangeAgent.orEmpty(),
        status = status,
        qrCode = qrCode.orEmpty(),
        isSchedulable = isSchedulable,
    )
