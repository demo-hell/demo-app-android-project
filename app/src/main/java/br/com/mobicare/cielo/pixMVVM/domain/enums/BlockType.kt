package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class BlockType {
    BANK_DOMICILE, PENNY_DROP, IN_PROGRESS;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}

