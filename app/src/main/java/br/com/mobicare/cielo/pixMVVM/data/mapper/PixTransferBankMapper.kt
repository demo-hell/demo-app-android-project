package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferBankResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank

fun PixTransferBankResponse.toEntity() = PixTransferBank(
    code = code,
    ispb = ispb,
    shortName = shortName,
    name = name
)

fun List<PixTransferBankResponse>.toEntity() = map { it.toEntity() }
