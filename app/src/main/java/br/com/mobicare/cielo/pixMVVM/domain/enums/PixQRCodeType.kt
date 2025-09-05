package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixQRCodeType(
    val transferType: PixTransferType,
) {
    STATIC(PixTransferType.QR_CODE_ESTATICO),
    DYNAMIC(PixTransferType.QR_CODE_DINAMICO),
    DYNAMIC_COBV(PixTransferType.QR_CODE_DINAMICO),
    ;

    companion object {
        fun find(value: String?) = values().firstOrNull { it.name == value } ?: STATIC
    }
}
