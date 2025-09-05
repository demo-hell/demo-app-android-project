package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.parseFromOffsetToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAccountBalanceResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance

fun PixAccountBalanceResponse.toEntity() = PixAccountBalance(
    currentBalance = balanceAvailableGlobal,
    timeOfRequest = timeOfRequest?.parseFromOffsetToZonedDateTime()
)