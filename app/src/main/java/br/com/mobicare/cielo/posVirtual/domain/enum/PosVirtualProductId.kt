package br.com.mobicare.cielo.posVirtual.domain.enum

enum class PosVirtualProductId(val value: String) {
    TAP_ON_PHONE("TAP-ON-PHONE"),
    PIX("PIX"),
    SUPERLINK_ADDITIONAL("SUPERLINK-ADDITIONAL"),
    POS_VIRTUAL("POS-VIRTUAL"),
    CARD_READER("CARD-READER");

    companion object {
        fun find(value: String?) = values().firstOrNull { it.value == value }
    }
}