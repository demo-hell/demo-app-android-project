package br.com.mobicare.cielo.pixMVVM.domain.model

data class PixRefundDetailFull(
    val refundDetail: PixRefundDetail? = null,
    val transferDetail: PixTransferDetail? = null,
    val enable: PixEnable? = null,
)
