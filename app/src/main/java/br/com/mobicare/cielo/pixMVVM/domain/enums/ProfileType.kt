package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class ProfileType {
    LEGACY, FREE_MOVEMENT, AUTOMATIC_TRANSFER, PARTNER_BANK;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}
