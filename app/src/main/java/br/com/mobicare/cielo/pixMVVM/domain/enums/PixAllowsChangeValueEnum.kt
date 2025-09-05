package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixAllowsChangeValueEnum(
    val isAllowed: Boolean,
) {
    NOT_ALLOWED(isAllowed = false),
    ALLOWED(isAllowed = true),
    ;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name } ?: NOT_ALLOWED
    }
}
