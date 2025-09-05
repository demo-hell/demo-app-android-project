package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixStatus {
    ACTIVE, PENDING, WAITING_ACTIVATION;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}
