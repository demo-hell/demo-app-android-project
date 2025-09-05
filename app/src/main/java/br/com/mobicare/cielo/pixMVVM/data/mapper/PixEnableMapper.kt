package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEnableResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable

fun PixEnableResponse.toEntity() =
    PixEnable(
        refund = refund,
        cancelSchedule = cancelSchedule,
        requestAnalysis = requestAnalysis,
    )
