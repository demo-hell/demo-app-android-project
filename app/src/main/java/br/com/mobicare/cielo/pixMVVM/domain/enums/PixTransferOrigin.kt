package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixTransferOrigin {
    P2M,
    APP,
    SITE,
    OPEN_FINANCE,
    CRD,
    KYC,
    SETTLEMENT_V2,
    FEE_V2,
    UNDEFINED;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}