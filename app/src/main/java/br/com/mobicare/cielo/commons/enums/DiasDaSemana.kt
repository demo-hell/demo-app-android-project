package br.com.mobicare.cielo.commons.enums

enum class DiasDaSemana(val dia: String) {
    SEGUNDA("Segunda-feira"),
    TERCA("Terça-feira"),
    QUARTA("Quarta-feira"),
    QUINTA("Quinta-feira"),
    SEXTA("Sexta-feira"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    companion object {
        fun fromString(value: String) = DiasDaSemana.values().find {
            it.dia == value
        }
    }
}
