package br.com.mobicare.cielo.pix.enums

import androidx.annotation.Keep

@Keep
enum class PixKeyTypeEnum(val id: Int, val key: String) {
    CNPJ(0, "CNPJ"),
    PHONE(1, "Celular"),
    EMAIL(2, "E-mail"),
    EVP(3, "Aleatória"),
    ACCOUNT(4, "Agência"),
    CPF(5, "CPF")
}