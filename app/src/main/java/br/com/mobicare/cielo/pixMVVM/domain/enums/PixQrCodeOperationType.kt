package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixQrCodeOperationType {
    TRANSFER,
    WITHDRAWAL,
    CHANGE,
    FEE,
    ;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}
