package br.com.mobicare.cielo.pix.enums

import androidx.annotation.Keep

@Keep
enum class BeneficiaryTypeEnum(val key: String, val personType: String) {
    CNPJ("J", "JURIDICA"),
    CPF("F", "FISICA")
}