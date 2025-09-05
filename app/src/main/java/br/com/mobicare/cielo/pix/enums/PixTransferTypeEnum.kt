package br.com.mobicare.cielo.pix.enums

enum class PixTransferTypeEnum(val code: Int) {
    NONE(code = -1),
    MANUAL(code = 0),
    CHAVE(code = 1),
    QR_CODE_ESTATICO(code = 2),
    QR_CODE_DINAMICO(code = 3),
    SERVICO_DE_INICIACAO_DE_TRANSACAO_DE_PAGAMENTO(code = 4);

    companion object {
        fun fromCode(code: Int?) = values().firstOrNull { it.code == code } ?: NONE
    }
}