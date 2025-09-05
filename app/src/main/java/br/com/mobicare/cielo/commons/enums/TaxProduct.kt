package br.com.mobicare.cielo.commons.enums

enum class TaxProduct(val value: String) {
    DEBITO_A_VISTA("Debito A Vista"),
    CREDITO_A_VISTA("Credito A Vista"),
    PARCELADO_LOJA("Parcelado Loja"),
    CREDITO_CONVERSOR_MOEDAS("Credito Conversor Moedas");

    companion object {
        fun fromString(value: String) = TaxProduct.values().find {
            it.value == value
        }
    }
}