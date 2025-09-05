package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixFeeType {
    PERCENTAGE, FIXED_AMOUNT;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}