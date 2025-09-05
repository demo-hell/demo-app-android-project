package br.com.mobicare.cielo.pix.enums

enum class PixAllowsChangeValueEnum(val isAllowed: Boolean) {
    NOT_ALLOWED(isAllowed = false), ALLOWED(isAllowed = true)
}