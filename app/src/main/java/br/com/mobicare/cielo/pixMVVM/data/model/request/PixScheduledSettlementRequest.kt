package br.com.mobicare.cielo.pixMVVM.data.model.request

data class PixScheduledSettlementRequest(
    val settlementScheduled: Boolean? = null,
    val listScheduled: List<String> = emptyList(),
)
