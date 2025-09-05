package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailFullResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull

fun PixRefundDetailFullResponse.toEntity() =
    PixRefundDetailFull(
        refundDetail = refundDetail?.toEntity(),
        transferDetail = transferDetail?.toEntity(),
        enable = enable?.toEntity(),
    )
