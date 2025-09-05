package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixTransferType {
    MANUAL,
    CHAVE,
    QR_CODE_ESTATICO,
    QR_CODE_DINAMICO,
    INICIADOR_DE_PAGAMENTO;

    companion object {
        fun find(code: Int?) = values().firstOrNull { it.ordinal == code }
    }
}