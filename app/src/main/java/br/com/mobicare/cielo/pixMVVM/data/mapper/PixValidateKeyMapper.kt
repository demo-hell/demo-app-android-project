package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixValidateKeyResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey

fun PixValidateKeyResponse.toEntity() = PixValidateKey(
    accountNumber = accountNumber.orEmpty(),
    accountType = accountType.orEmpty(),
    branch = branch.orEmpty(),
    claimType = claimType.orEmpty(),
    creationDate = creationDate.orEmpty(),
    endToEndId = endToEndId.orEmpty(),
    key = key.orEmpty(),
    keyType = keyType.orEmpty(),
    ownerDocument = ownerDocument.orEmpty(),
    ownerName = ownerName.orEmpty(),
    ownerTradeName = ownerTradeName.orEmpty(),
    ownerType = ownerType.orEmpty(),
    ownershipDate = ownershipDate.orEmpty(),
    participant = participant.orEmpty(),
    participantName = participantName.orEmpty(),
)