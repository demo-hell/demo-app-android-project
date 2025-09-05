package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixRefundDetailFullResponse(
    val refundDetail: PixRefundDetailResponse?,
    val transferDetail: PixTransferDetailResponse?,
    val enable: PixEnableResponse? = null,
)
