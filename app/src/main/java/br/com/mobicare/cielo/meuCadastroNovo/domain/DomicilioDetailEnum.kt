package br.com.mobicare.cielo.meuCadastroNovo.domain

import br.com.mobicare.cielo.R

enum class DomicilioDetailEnum(val statusCode: Int, val statusLabel: String) {

    PENDING(1, "Em Andamento") {
        override fun getColor() = R.color.color_f98f25
    },
    APPROVED(2, "Aprovado") {
        override fun getColor() = R.color.color_009e55
    },
    CANCEL(3, "Rejeitado") {
        override fun getColor() = R.color.red_DC392A
    },
    UNKNOWN(4, "Desconhecido"){
        override fun getColor() = R.color.red_DC392A
    };

    abstract fun getColor() : Int
}